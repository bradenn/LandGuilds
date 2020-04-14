package xyz.dec0de.landguilds.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Tags;
import xyz.dec0de.landguilds.storage.GuildStorage;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DynmapHandler {

    private static DynmapAPI api;
    private static MarkerSet set;

    public static void initDynmap(Server _server) {
        PluginManager pm = _server.getPluginManager();

        Plugin dynmap = pm.getPlugin("dynmap");
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
                int color;
                ChatColor guildColor = guildStorage.getColor();
                Color col = org.bukkit.Color.fromRGB(colorMap.get(guildColor).getRed(),
                        colorMap.get(guildColor).getGreen(),
                        colorMap.get(guildColor).getBlue());
                color = col.asRGB();
                stringBuilder.append("<b>")
                        .append(guildStorage.getName()).append("</b>")
                        .append("<br>Color: ").append(guildStorage.getColor().name())
                        .append("<br>Chunks: ").append(guildStorage.getChunks().size())
                        .append("<br>PVP: ").append(guildStorage.getTag(Tags.PVP));
                set.createAreaMarker("guild.claim." + bitX + ":" + bitZ, stringBuilder.toString(), true, chunk.getWorld().getName(), xArray, zArray, false);
                set.findAreaMarker("guild.claim." + bitX + ":" + bitZ).setLineStyle(2, 1, color);
                set.findAreaMarker("guild.claim." + bitX + ":" + bitZ).setFillStyle(0.3, color);
            });
        }

    }

    private static Map<ChatColor, ColorSet<Integer, Integer, Integer>> colorMap = new HashMap<>();

    static {
        colorMap.put(ChatColor.BLACK, new ColorSet<>(0, 0, 0));
        colorMap.put(ChatColor.DARK_BLUE, new ColorSet<>(0, 0, 170));
        colorMap.put(ChatColor.DARK_GREEN, new ColorSet<>(0, 170, 0));
        colorMap.put(ChatColor.DARK_AQUA, new ColorSet<>(0, 170, 170));
        colorMap.put(ChatColor.DARK_RED, new ColorSet<>(170, 0, 0));
        colorMap.put(ChatColor.DARK_PURPLE, new ColorSet<>(170, 0, 170));
        colorMap.put(ChatColor.GOLD, new ColorSet<>(255, 170, 0));
        colorMap.put(ChatColor.GRAY, new ColorSet<>(170, 170, 170));
        colorMap.put(ChatColor.DARK_GRAY, new ColorSet<>(85, 85, 85));
        colorMap.put(ChatColor.BLUE, new ColorSet<>(85, 85, 255));
        colorMap.put(ChatColor.GREEN, new ColorSet<>(85, 255, 85));
        colorMap.put(ChatColor.AQUA, new ColorSet<>(85, 255, 255));
        colorMap.put(ChatColor.RED, new ColorSet<>(255, 85, 85));
        colorMap.put(ChatColor.LIGHT_PURPLE, new ColorSet<>(255, 85, 255));
        colorMap.put(ChatColor.YELLOW, new ColorSet<>(255, 255, 85));
        colorMap.put(ChatColor.WHITE, new ColorSet<>(255, 255, 255));
    }

    private static class ColorSet<R, G, B> {
        R red;
        G green;
        B blue;

        ColorSet(R red, G green, B blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public R getRed() {
            return red;
        }

        public G getGreen() {
            return green;
        }

        public B getBlue() {
            return blue;
        }

    }

    //Override accept method
    static FileFilter ymlFileFilter = file -> {
        //if the file extension is .log return true, else false
        return file.getName().endsWith(".yml");
    };

}