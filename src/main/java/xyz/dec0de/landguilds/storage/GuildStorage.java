package xyz.dec0de.landguilds.storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Relationship;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.enums.Tags;

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
                configuration.set("money", 0.00);
                configuration.set("tokens", 0);
                configuration.set("created_timestamp", System.currentTimeMillis());
                configuration.set("color", ChatColor.GRAY.name());
                configuration.set("level", 1);
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
    public GuildStorage(String name, UUID owner) throws IOException {
        this(UUID.randomUUID());
        setName(name);

        config.set("members." + owner.toString() + ".role", "OWNER");
        config.save(file);
    }

    /**
     * Get uuid of guild
     *
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the members of a chunk
     *
     * @return
     * @throws NullPointerException
     */

    public List<UUID> getMembers() throws NullPointerException {
        ConfigurationSection section = config.getConfigurationSection("members");
        List<UUID> members = new ArrayList<>();

        if (config.getConfigurationSection("members") != null) {
            for (String uuids : section.getKeys(false)) {
                members.add(UUID.fromString(uuids));
            }
        }

        return members;
    }


    /**
     * Get the role of a player in a chunk.
     *
     * @param uuid
     * @return
     */
    public Role getRole(UUID uuid) {
        Role roles = Role.valueOf(config.getString("members." + uuid.toString() + ".role"));

        return roles;
    }

    public void setTag(Tags tag, boolean status) {
        config.set("tags." + tag.toString(), status);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getTag(Tags tag) {
        if (config.contains("tags." + tag.toString() + "")) {
            return config.getBoolean("tags." + tag.toString() + "");
        } else {
            return true;
        }
    }

    /**
     * Set the role of players
     *
     * @param uuid
     * @param role
     * @return
     */
    public void setRole(UUID uuid, Role role) throws IOException {
        if (getMembers().contains(uuid)) {
            config.set("members." + uuid.toString() + ".role", role.toString());
            config.save(file);
        }
    }

    /**
     * Get the owner of the chunk
     *
     * @return
     */

    public UUID getOwner() {
        UUID owner = null;

        for (UUID member : getMembers()) {
            if (config.getString("members." + member.toString() + ".role").equalsIgnoreCase("OWNER")) {
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
        if (!getMembers().contains(uuid)) {
            config.set("members." + uuid.toString() + ".role", "MEMBER");
            config.save(file);

            return true;
        }

        return false;
    }

    /**
     * Remove another player from the chunk
     *
     * @param uuid
     * @return
     * @throws IOException
     */

    public boolean removeMember(UUID uuid) throws IOException {
        if (getMembers().contains(uuid)) {
            config.set("members." + uuid.toString(), null);
            config.save(file);

            return true;
        }

        return false;
    }

    /**
     * Declare another guild as an enemy or a friend
     *
     * @param uuid
     * @return
     * @throws IOException
     */

    public void setRelationship(UUID uuid, Relationship relationship) throws IOException {
        if (relationship != Relationship.NEUTRAL) {
            config.set("relationships." + uuid.toString(), Relationship.ALLY.toString());
        } else {
            config.set("relationships." + uuid.toString(), null);
        }
        config.save(file);
    }

    /**
     * Declare another guild as an enemy or a friend
     *
     * @param uuid
     * @return
     * @throws IOException
     */

    public Relationship getRelationship(UUID uuid) {
        if (config.contains("relationships." + uuid.toString())) {
            return Relationship.valueOf(config.getString("relationships." + uuid.toString()));
        } else {
            return Relationship.NEUTRAL;
        }
    }

    /**
     * Get the name of the guild
     *
     * @return
     */
    public String getName() {
        return config.getString("name");
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

    /**
     * Get the guild tag
     *
     * @return
     */
    public String getTag() {
        return getColor() + "[" + getName().toUpperCase() + "]";
    }

    public ChatColor getColor() {
        return ChatColor.valueOf(config.getString("color"));
    }

    public void setColor(ChatColor color) throws IOException {
        config.set("color", color.toString());
        config.save(file);
    }

    public List<Chunk> getChunks() {
        List<Chunk> chunkList = new ArrayList<>();
        List<String> list = config.getStringList("chunks");
        for (String chnk : list) {
            String world = chnk.split(";")[0];
            int chunkX = Integer.parseInt(chnk.split(";")[1]);
            int chunkZ = Integer.parseInt(chnk.split(";")[2]);

            chunkList.add(Bukkit.getWorld(world).getChunkAt(chunkX, chunkZ));
        }

        return chunkList;
    }

    //TODO Make it claim the chunk or remove it too
    public boolean addChunk(World world, Chunk chunk) throws IOException {
        ChunkStorage chunkStorage = new ChunkStorage(world, chunk);
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");
            list.add(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
            return true;
        }

        return false;
    }

    public boolean removeChunk(World world, Chunk chunk) throws IOException {
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");

            list.remove(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
            return true;
        }

        return false;
    }

    public boolean disbandGuild() throws IOException {
        List<String> chunks = config.getStringList("chunks");
        for (String chnk : chunks) {
            World world = Bukkit.getWorld(chnk.split(";")[0]);
            Chunk chunk = world.getChunkAt(Integer.parseInt(chnk.split(";")[1]), Integer.parseInt(chnk.split(";")[2]));

            ChunkStorage chunkStorage = new ChunkStorage(world, chunk);
            chunkStorage.unclaim();
        }

        for (UUID uuid : getMembers()) {
            PlayerStorage playerStorage = new PlayerStorage(uuid);
            playerStorage.removeGuild();
        }

        file.delete();
        return true;
    }

    public boolean get(Tags valueOf) {
        return config.getBoolean("tags."+valueOf.toString());
    }
}
