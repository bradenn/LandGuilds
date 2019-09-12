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

public class GuildStorage {

    private UUID uuid;
    private File file;
    private FileConfiguration config;

    /**
     * Gets a config based on guild UUID
     *
     * @param uuid
     */
    public GuildStorage(UUID uuid) {
        this.uuid = uuid;

        // plugins/ArchesLands/chunks/world_name/chunk_x;chunk_z.yml
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "guilds" + File.separator, uuid.toString() + ".yml");

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
     * Creates a new guild
     *
     * @param name
     */
    public GuildStorage(String name) throws IOException {
        this(UUID.randomUUID());
        setName(name);
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
        if(!getMembers().contains(uuid)){
            config.set("members." + uuid.toString() + ".role", "MEMBER");
            config.save(file);

            return true;
        }

        return false;
    }

    /**
     * Set the name of a guild
     *
     * @param name
     * @throws IOException
     */
    public void setName(String name) throws IOException {
        config.set("name", name);
        config.save(file);
    }

}
