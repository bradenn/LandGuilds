package xyz.dec0de.archesmc.lands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dec0de.archesmc.lands.commands.GuildsCommand;
import xyz.dec0de.archesmc.lands.commands.LandsCommand;
import xyz.dec0de.archesmc.lands.events.ChunkEvents;
import xyz.dec0de.archesmc.lands.events.PlayerEvents;

import java.util.List;

public class Main extends JavaPlugin{

    private static Main plugin;
    public static FileConfiguration config;

    /**
     * TODO
     *  - Check chunk if it's a "guild" chunk, and store a boolean for guild
     *  - Instead of storing the members of a chunk in the guilds, it will be stored in guilds and refer back and forth
     *  - For storing which guild they're apart of, there will be a variable set in each player's config files
     *  with the guild's UUID?
     */

    @Override
    public void onEnable(){
        plugin = this;
        config = this.getConfig();

        plugin.saveDefaultConfig();

        plugin.getCommand("lands").setExecutor(new LandsCommand());
        plugin.getCommand("guilds").setExecutor(new GuildsCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChunkEvents(), plugin);
    }

    @Override
    public void onDisable(){

    }

    public static Main getPlugin(){
        return plugin;
    }

    public static List<String> allowedWorlds(){
        return config.getStringList("worlds");
    }
}
