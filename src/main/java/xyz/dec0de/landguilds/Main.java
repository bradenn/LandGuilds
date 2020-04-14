package xyz.dec0de.landguilds;

import com.avaje.ebean.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import xyz.dec0de.landguilds.commands.*;
import xyz.dec0de.landguilds.events.ChunkEvents;
import xyz.dec0de.landguilds.events.InventoryEvents;
import xyz.dec0de.landguilds.events.PlayerEvents;
import xyz.dec0de.landguilds.handlers.DynmapHandler;
import xyz.dec0de.landguilds.handlers.InventoryHandler;
import xyz.dec0de.landguilds.storage.GuildStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static FileConfiguration config;
    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    public static List<String> allowedWorlds() {
        return config.getStringList("worlds");
    }

    @Override
    public void onDisable() {

    }

    /**
     * TODO
     * Guilds:
     * - Disbanding Guilds (only for owners)
     * - Transfering ownership
     * - Levels (xp ranking up system for earning more
     * guild claims & colors and ability to change prefix)
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
        DynmapHandler.initDynmap(getServer());
        DynmapHandler.reloadGuildChunks();
        plugin.getCommand("lands").setExecutor(new LandsCommand());
        plugin.getCommand("guilds").setExecutor(new GuildsCommand());
        plugin.getCommand("map").setExecutor(new MapCommand());
        plugin.getCommand("lgadmin").setExecutor(new AdminCommand());
        plugin.getCommand("landguilds").setExecutor(new LandGuildsCommand());

        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChunkEvents(), plugin);
    }
}
