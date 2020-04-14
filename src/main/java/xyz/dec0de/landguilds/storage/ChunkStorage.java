package xyz.dec0de.landguilds.storage;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Role;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChunkStorage {
    private final static Map<ChunkStorageContainer, ChunkStorage> chunkCache = new HashMap<>();

    public static ChunkStorage getChunk(World world, Chunk chunk) {
        ChunkStorageContainer container = new ChunkStorageContainer(world.getUID(), chunk.getX(), chunk.getZ());
        if (chunkCache.containsKey(container)) {
            return chunkCache.get(container);
        } else {
            ChunkStorage storage = new ChunkStorage(world, chunk);
            chunkCache.put(container, storage);
            return storage;
        }
    }

    private final World world;
    private final Chunk chunk;
    private final File file;
    private final List<UUID> members;
    private FileConfiguration config;
    private boolean claimed;

    private ChunkStorage(World world, Chunk chunk) {
        this.world = world;
        this.chunk = chunk;

        // plugins/ArchesLands/chunks/world_name/chunk_x;chunk_z.yml
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "chunks" + File.separator + world.getName() + File.separator, chunk.getX() + ";" + chunk.getZ() + ".yml");

        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);

            ConfigurationSection section = config.getConfigurationSection("members");
            members = new ArrayList<>();
            if (section != null) {
                for (String uuid : section.getKeys(false)) {
                    members.add(UUID.fromString(uuid));
                }
            }
            claimed = members.size() >= 1;
            if (!claimed) {
                file.delete();
            }
        } else {
            members = new ArrayList<>();
            claimed = false;
        }
    }

    /**
     * Get the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the chunk
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * Check if a chunk is claimed
     */
    public boolean isClaimed() {
        return claimed;
    }

    /**
     * Get the members of a chunk
     */
    public List<UUID> getMembers() throws NullPointerException {
        return members;
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
        if (!getMembers().contains(uuid) && isClaimed()) {
            members.add(uuid);
            config.set("members." + uuid.toString() + ".role", "MEMBER");
            config.save(file);
        }
    }

    /**
     * Remove another player from the chunk
     */
    public void removeMember(UUID uuid) throws IOException {
        if (getMembers().contains(uuid)) {
            members.remove(uuid);
            config.set("members." + uuid.toString(), null);
            config.save(file);
        }
    }

    /**
     * Claim a chunk
     */
    public void claim(UUID uuid, boolean guild) throws IOException {
        if (!(file.exists())) {
            try {
                config = YamlConfiguration.loadConfiguration(file);
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isClaimed() && Main.allowedWorlds().contains(world.getName())) {
            claimed = true;
            config.set("members." + uuid.toString() + ".role", "OWNER");

            if (guild) {
                config.set("guild", true);
            }

            config.save(file);
        }
    }

    /**
     * Get the role of a player in a chunk.
     */
    public Role getRole(UUID uuid) {
        return Role.valueOf(config.getString("members." + uuid.toString() + ".role"));
    }

    /**
     * Unclaim a chunk and remove all members
     */
    public void unclaim() {
        if (isClaimed() && Main.allowedWorlds().contains(world.getName())) {
            file.delete();
            claimed = false;
            chunkCache.remove(new ChunkStorageContainer(world.getUID(), chunk.getX(), chunk.getZ()));
        }
    }

    public boolean isGuild() {
        if (config.isSet("guild")) {
            return config.getBoolean("guild");
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkStorage that = (ChunkStorage) o;
        return Objects.equals(world.getUID(), that.world.getUID()) &&
                Objects.equals(chunk, that.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world.getUID(), chunk);
    }

    private static class ChunkStorageContainer {
        private final UUID world;
        private final int x, z;

        private ChunkStorageContainer(UUID world, int x, int z) {
            this.world = world;
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkStorageContainer that = (ChunkStorageContainer) o;
            return x == that.x &&
                    z == that.z &&
                    Objects.equals(world, that.world);
        }

        @Override
        public int hashCode() {
            return Objects.hash(world, x, z);
        }
    }
}
