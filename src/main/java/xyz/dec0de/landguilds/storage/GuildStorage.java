package xyz.dec0de.landguilds.storage;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Roles;

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
    public Roles getRole(UUID uuid) {
        Roles roles = Roles.valueOf(config.getString("members." + uuid.toString() + ".role"));

        return roles;
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

}
