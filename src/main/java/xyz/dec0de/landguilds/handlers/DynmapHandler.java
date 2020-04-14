package xyz.dec0de.landguilds.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.GuildStorage;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Objects;
import java.util.UUID;

public class DynmapHandler {

    private static Plugin dynmap;
    private static DynmapAPI api;
    private static MarkerSet set;
    private static Server server;

    public static void initDynmap(Server _server) {
        server = _server;
        PluginManager pm = server.getPluginManager();

        dynmap = pm.getPlugin("dynmap");
        if (dynmap == null) {
            Bukkit.broadcastMessage("Cannot find dynmap!");
            return;
        }
        api = (DynmapAPI) dynmap;
    }

    public static void reloadGuildChunks() {
        MarkerAPI markerAPI = api.getMarkerAPI();
        if (markerAPI == null) {
            Bukkit.broadcastMessage("Error loading dynmap marker API!");
            return;
        }
        set = markerAPI.getMarkerSet("guild.claim");
        if (set == null)
            set = markerAPI.createMarkerSet("guild.claim", "Guilds", null, false);
        else
            set.setMarkerSetLabel("Guilds");
        File f = new File(Main.getPlugin().getDataFolder() + File.separator + "guilds");
        for (File file : Objects.requireNonNull(f.listFiles(ymlFileFilter))) {
            GuildStorage guildStorage = new GuildStorage(UUID.fromString(file.getName().replace(".yml", "")));
            guildStorage.getChunks().forEach(chunk -> {
                double bitX = chunk.getX() * 16;
                double bitZ = chunk.getZ() * 16;
                double[] xArray = {bitX, bitX + 16};
                double[] zArray = {bitZ, bitZ + 16};
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<b>")
                        .append(guildStorage.getName()).append("</b>");
                set.createAreaMarker("guild.claim." + bitX + ":" + bitZ, guildStorage.getName(), true, chunk.getWorld().getName(), xArray, zArray, false);
                int color = 240116;
                if(guildStorage.getName().equalsIgnoreCase("bland")){
                    color = 16007990;
                }
                set.findAreaMarker("guild.claim." + bitX + ":" + bitZ).setLineStyle(2, 1, color);
                set.findAreaMarker("guild.claim." + bitX + ":" + bitZ).setFillStyle(0.3, color);
            });
        }

    }

    static FileFilter ymlFileFilter = new FileFilter() {
        //Override accept method
        public boolean accept(File file) {
            //if the file extension is .log return true, else false
            if (file.getName().endsWith(".yml")) {
                return true;
            }
            return false;
        }
    };
}