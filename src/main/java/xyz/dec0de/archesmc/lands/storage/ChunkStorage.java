package xyz.dec0de.archesmc.lands.storage;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.dec0de.archesmc.lands.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkStorage {

    private World world;
    private Chunk chunk;
    private File file;
    private FileConfiguration config;

    public ChunkStorage(World world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;

        // plugins/ArchesLands/chunks/world_name/chunk_x;chunk_z.yml
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "chunks" + File.separator + world.getName() + File.separator, chunk.getX() + ";" + chunk.getZ() + ".yml");

        if (!(file.exists())) {
            try {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get the world
     * @return
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the chunk
     * @return
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * Check if a chunk is claimed
     *
     * @return
     */
    public boolean isClaimed(){
        if(getMembers().size() >= 1){
            return true;
        }
        return false;
    }

    /**
     * Get the members of a chunk
     *
     * @return
     * @throws NullPointerException
     */

    public List<UUID> getMembers() throws NullPointerException{
        ConfigurationSection section = config.getConfigurationSection("members");
        List<UUID> members = new ArrayList<>();

        if(config.getConfigurationSection("members") != null) {
            for (String uuids : section.getKeys(false)) {
                members.add(UUID.fromString(uuids));
            }
        }

        return members;
    }

    /**
     * Get the owner of the chunk
     * @return
     */

    public UUID getOwner(){
        UUID owner = null;

        for (UUID member : getMembers()) {
            if(config.getString("members." + member.toString() + ".role").equalsIgnoreCase("OWNER")){
                owner = member;
                break;
            }
        }
        return owner;
    }

    /**
     * Allow another player the chunk
     *
     * @param uuid
     * @return
     * @throws IOException
     */

    public boolean addMember(UUID uuid) throws IOException {
        if(!getMembers().contains(uuid) && isClaimed()){
            config.set("members." + uuid.toString() + ".role", "MEMBER");
            config.save(file);

            return true;
        }

        return false;
    }

    /**
     * Claim a chunk
     * @param uuid
     * @return
     * @throws IOException
     */

    public boolean claim(UUID uuid, boolean guild) throws IOException {
        if(!isClaimed() && Main.allowedWorlds().contains(world.getName())){
            config.set("members." + uuid.toString() + ".role", "OWNER");

            if(guild){
                config.set("guild", true);
            }

            config.save(file);
            return true;
        }

        return false;
    }

    /**
     * Unclaim a chunk and remove all members
     *
     * @return
     * @throws IOException
     */

    public boolean unclaim() throws IOException {
        if(isClaimed() && Main.allowedWorlds().contains(world.getName())){
            for(UUID uuids : getMembers()){
                config.set("members." + uuids.toString(), null);
            }

            if(isGuild()){
                config.set("guild", null);
            }

            config.save(file);

            return true;
        }

        return false;
    }

    public boolean isGuild(){
        if(config.isSet("guild")){
            return config.getBoolean("guild");
        }
        return false;
    }
}
