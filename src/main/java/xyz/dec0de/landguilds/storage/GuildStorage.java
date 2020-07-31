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
import java.util.Objects;
import java.util.UUID;

public class GuildStorage {
    private UUID uuid;
    private File file;
    private FileConfiguration config;

    /**
     * Gets a config based on guild UUID
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
     */
    public GuildStorage(String name, UUID owner) throws IOException {
        this(UUID.randomUUID());
        setName(name);

        config.set("members." + owner.toString() + ".role", "OWNER");
        config.save(file);
    }

    /**
     * Get uuid of guild
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the members of a chunk
     */
    public List<UUID> getMembers() throws NullPointerException {
        ConfigurationSection section = config.getConfigurationSection("members");
        List<UUID> members = new ArrayList<>();

        if (config.getConfigurationSection("members") != null) {
            assert section != null;
            for (String uuid : section.getKeys(false)) {
                members.add(UUID.fromString(uuid));
            }
        }

        return members;
    }


    /**
     * Get the role of a player in a chunk.
     */
    public Role getRole(UUID uuid) {
        return Role.valueOf(config.getString("members." + uuid.toString() + ".role"));
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

    public void getRelationships() {

    }

    /**
     * Set the role of players
     */
    public void setRole(UUID uuid, Role role) throws IOException {
        if (getMembers().contains(uuid)) {
            config.set("members." + uuid.toString() + ".role", role.toString());
            config.save(file);
        }
    }

    /**
     * Get the owner of the chunk
     */
    public UUID getOwner() {
        UUID owner = null;

        for (UUID member : getMembers()) {
            if (Objects.requireNonNull(config.getString("members." + member.toString() + ".role")).equalsIgnoreCase("OWNER")) {
                owner = member;
                break;
            }
        }
        return owner;
    }

    /**
     * Allow another player the chunk
     */
    public void addMember(UUID uuid) throws IOException {
        if (!getMembers().contains(uuid)) {
            config.set("members." + uuid.toString() + ".role", "MEMBER");
            config.save(file);
        }
    }

    /**
     * Remove another player from the chunk
     */
    public void removeMember(UUID uuid) throws IOException {
        if (getMembers().contains(uuid)) {
            config.set("members." + uuid.toString(), null);
            config.save(file);
        }
    }

    /**
     * Declare another guild as an enemy or a friend
     */

    public void setRelationship(UUID uuid, Relationship relationship) throws IOException {
        List<String> enemies = config.getStringList("enemies");
        List<String> allies = config.getStringList("allies");
        if (relationship != Relationship.NEUTRAL) {
            if (relationship == Relationship.ENEMY) {
                if (!enemies.contains(uuid)) {
                    enemies.add(uuid.toString());
                    if (allies.contains(uuid.toString())) {
                        allies.remove(uuid.toString());
                    }
                }
            } else if (relationship == Relationship.ALLY) {
                if (!allies.contains(uuid)) {
                    allies.add(uuid.toString());
                    if (enemies.contains(uuid.toString())) {
                        enemies.remove(uuid.toString());
                    }
                }
            }
        } else {
            if (allies.contains(uuid.toString())) {
                allies.remove(uuid.toString());
            }
            if (enemies.contains(uuid.toString())) {
                enemies.remove(uuid.toString());
            }
        }
        config.save(file);
    }

    /**
     * Declare another guild as an enemy or a friend
     */
    public Relationship getRelationship(UUID uuid) {
        List<String> enemies = config.getStringList("enemies");
        List<String> allies = config.getStringList("allies");
        if(enemies.contains(uuid.toString())){
            return Relationship.ENEMY;
        }else if(allies.contains(uuid.toString())){
            return Relationship.ALLY;
        }else{
            return Relationship.NEUTRAL;
        }
    }

    public List<GuildStorage> getEnemies(){
        List<String> enemies = config.getStringList("enemies");
        List<GuildStorage> guilds = new ArrayList<>();
        enemies.forEach(guild -> guilds.add(new GuildStorage(UUID.fromString(guild))));
        return guilds;
    }

    /**
     * Get the name of the guild
     */
    public String getName() {
        return config.getString("name");
    }

    /**
     * Set the name of a guild
     */
    public void setName(String name) throws IOException {
        config.set("name", name);
        config.save(file);
    }

    /**
     * Get the guild tag
     */
    public String getTag() {
        return getColor() + "[" + getName() + "]";
    }

    public ChatColor getColor() {
        return ChatColor.valueOf(config.getString("color"));
    }

//    public void setColor(ChatColor color) throws IOException {
//        config.set("color", color.toString());
//        config.save(file);
//    }

    public List<Chunk> getChunks() {
        List<Chunk> chunkList = new ArrayList<>();
        List<String> list = config.getStringList("chunks");
        for (String chunkString : list) {
            String world = chunkString.split(";")[0];
            int chunkX = Integer.parseInt(chunkString.split(";")[1]);
            int chunkZ = Integer.parseInt(chunkString.split(";")[2]);

            chunkList.add(Objects.requireNonNull(Bukkit.getWorld(world)).getChunkAt(chunkX, chunkZ));
        }
        return chunkList;
    }

    //TODO Make it claim the chunk or remove it too
    public void addChunk(World world, Chunk chunk) throws IOException {
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");
            list.add(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
        }
    }

    public void removeChunk(World world, Chunk chunk) throws IOException {
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");

            list.remove(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
        }
    }

    public void disbandGuild() throws IOException {
        List<String> chunks = config.getStringList("chunks");
        for (String chunkString : chunks) {
            World world = Bukkit.getWorld(chunkString.split(";")[0]);
            assert world != null;
            Chunk chunk = world.getChunkAt(Integer.parseInt(chunkString.split(";")[1]), Integer.parseInt(chunkString.split(";")[2]));

            ChunkStorage chunkStorage = ChunkStorage.getChunk(world, chunk);
            chunkStorage.unclaim();
        }

        for (UUID uuid : getMembers()) {
            PlayerStorage playerStorage = new PlayerStorage(uuid);
            playerStorage.removeGuild();
        }

        file.delete();
    }

    public boolean get(Tags valueOf) {
        return config.getBoolean("tags." + valueOf.toString());
    }
}
