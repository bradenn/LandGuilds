package xyz.dec0de.landguilds;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dec0de.landguilds.commands.*;
import xyz.dec0de.landguilds.events.ChunkEvents;
import xyz.dec0de.landguilds.events.InventoryEvents;
import xyz.dec0de.landguilds.events.PlayerEvents;
import xyz.dec0de.landguilds.gui.GuildMenuView;
import xyz.dec0de.landguilds.handlers.DynmapHandler;
import xyz.dec0de.landguilds.utils.PlaceholderAPI;

import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static FileConfiguration config;
    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    public static List<String> allowedWorlds() {
        return config.getStringList("worlds");
    }

    public static int getMaxLandClaims() {
        return config.getInt("maxlandclaims");
    }

    @Override
    public void onDisable() {

    }

    /**
     * TODO
     * Guilds:
     * - Transfering ownership
     * - GUIs (Members list, etc)
     * Lands:
     * - Homes
     * - Permissions for claims and such
     */

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();
        plugin.saveDefaultConfig();
        new DynmapHandler().reloadAllChunks();
        Objects.requireNonNull(plugin.getCommand("lands")).setExecutor(new LandsCommand());
        Objects.requireNonNull(plugin.getCommand("guilds")).setExecutor(new GuildsCommand());
        Objects.requireNonNull(plugin.getCommand("guilds")).setTabCompleter(new GuildsTabMenu());
        Objects.requireNonNull(plugin.getCommand("map")).setExecutor(new MapCommand());
        Objects.requireNonNull(plugin.getCommand("lgadmin")).setExecutor(new AdminCommand());
        Objects.requireNonNull(plugin.getCommand("landguilds")).setExecutor(new LandGuildsCommand());

        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChunkEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new GuildMenuView(), plugin);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderAPI(this).register();
        }
    }
}
