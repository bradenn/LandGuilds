package xyz.dec0de.landguilds.storage;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerStorage {
    private File file;
    private FileConfiguration config;

    public PlayerStorage(UUID uuid) {
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "players" + File.separator, uuid.toString() + ".yml");

        if (!(file.exists())) {
            try {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

                List<String> list = new ArrayList<>();
                configuration.set("chunks", list);

                configuration.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerStorage(Player player) throws IOException {
        this(player.getUniqueId());

        config.set("username", player.getName());
        config.save(file);
    }

    public String getUsername() {
        return config.getString("username");
    }

    /**
     * Get the claimed chunks
     */
    /*public List<Chunk> getChunks() {
        List<Chunk> chunkList = new ArrayList<>();
        List<String> list = config.getStringList("chunks");
        for (String chunkString : list) {
            String world = chunkString.split(";")[0];
            int chunkX = Integer.parseInt(chunkString.split(";")[1]);
            int chunkZ = Integer.parseInt(chunkString.split(";")[2]);

            chunkList.add(Objects.requireNonNull(Bukkit.getWorld(world)).getChunkAt(chunkX, chunkZ));
        }

        return chunkList;
    }*/

    //TODO Make it claim the chunk or remove it too
    public void addChunk(World world, Chunk chunk) throws IOException {
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");
            list.add(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
        }
    }

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

    public void removeChunk(World world, Chunk chunk) throws IOException {
        if (Main.allowedWorlds().contains(world.getName())) {
            List<String> list = config.getStringList("chunks");
            list.remove(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());
            config.set("chunks", list);
            config.save(file);
        }
    }

    public void removeGuild() throws IOException {
        config.set("guild", null);
        config.save(file);
    }

    public GuildStorage getGuild() {
        if (config.isSet("guild")) {
            return new GuildStorage(UUID.fromString(Objects.requireNonNull(config.getString("guild"))));
        }

        return null;
    }

    public void setGuild(UUID uuid) throws IOException {
        config.set("guild", uuid.toString());
        config.save(file);
    }
}
