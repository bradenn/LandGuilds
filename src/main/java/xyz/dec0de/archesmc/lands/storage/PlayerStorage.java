package xyz.dec0de.archesmc.lands.storage;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.dec0de.archesmc.lands.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage {

    private UUID uuid;
    private File file;
    private FileConfiguration config;

    public PlayerStorage(UUID uuid) {
        this.uuid = uuid;

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

    public String getUsername(){
        return config.getString("username");
    }

    public boolean addChunk(World world, Chunk chunk) throws IOException {
        ChunkStorage chunkStorage = new ChunkStorage(world, chunk);
        if(Main.allowedWorlds().contains(world.getName())){
            List<String> list = config.getStringList("chunks");
            list.add(world.getName() + ";" + chunk.getX() + ";" + chunk.getZ());

            config.set("chunks", list);
            config.save(file);
            return true;
        }

        return false;
    }

    public void setGuild(UUID uuid) throws IOException {
        config.set("guild", uuid);
        config.save(file);
    }
}
