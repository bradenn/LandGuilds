package xyz.dec0de.landguilds.handlers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;
import xyz.dec0de.landguilds.utils.ColorUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;
import java.util.UUID;

public class DynmapHandler {

    static FileFilter ymlFileFilter = file -> {
        //if the file extension is .yml return true, else false
        return file.getName().endsWith(".yml");
    };
    private DynmapAPI api;
    private MarkerSet set;

    /*
     * Connect to dynmap plugin
     */
    public DynmapHandler() {
        PluginManager pm = Bukkit.getServer().getPluginManager();

        Plugin dynmap = pm.getPlugin("dynmap");
        if (dynmap == null) {
            Bukkit.broadcastMessage("Cannot find dynmap!");
        }
        api = (DynmapAPI) dynmap;
        loadMarkerApi();
    }

    /*
     * Create Marker instance
     */
    private void loadMarkerApi() {
        MarkerAPI markerAPI = api.getMarkerAPI();
        if (markerAPI == null) {
            Bukkit.broadcastMessage("Error loading dynmap marker API!");
        }
        set = markerAPI.getMarkerSet("claims");
        if (set == null)
            set = markerAPI.createMarkerSet("claims", "Guilds & Land", null, false);
        else
            set.setMarkerSetLabel("Guilds & Land");
    }

    public void reloadAllChunks() {
        reloadAllGuildChunks();
        reloadAllLandChunks();
    }

    /*
     *   Get all chunks belonging to guilds, then render them with markers
     */
    public void reloadAllGuildChunks() {
        File f = new File(Main.getPlugin().getDataFolder() + File.separator + "guilds");
        for (File file : Objects.requireNonNull(f.listFiles(ymlFileFilter))) {
            GuildStorage guildStorage = new GuildStorage(UUID.fromString(file.getName().replace(".yml", "")));
            guildStorage.getChunks().forEach(this::reloadChunk);
        }
    }

    public void reloadAllLandChunks() {
        File f = new File(Main.getPlugin().getDataFolder() + File.separator + "players");
        for (File file : Objects.requireNonNull(f.listFiles(ymlFileFilter))) {
            PlayerStorage playerStorage = new PlayerStorage(UUID.fromString(file.getName().replace(".yml", "")));
            playerStorage.getChunks().forEach(this::reloadChunk);
        }
    }

    private String buildGuildMarkup(GuildStorage guildStorage) {
        return "<b>" + guildStorage.getName() + "</b>" +
                "<br>Chunks: " + guildStorage.getChunks().size();
    }

    private String buildLandMarkup(PlayerStorage playerStorage) {
        return "<b>" + playerStorage.getUsername() + "</b>" +
                "<br>Chunks: " + playerStorage.getChunks().size();
    }

    /*
     * Reload on chunk's marker on dynmap
     */
    public void reloadChunk(Chunk chunk) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(chunk.getWorld(), chunk);
        double bitX = chunk.getX() * 16;
        double bitZ = chunk.getZ() * 16;
        double[] xArray = {bitX, bitX + 16};
        double[] zArray = {bitZ, bitZ + 16};
        if (chunkStorage.isClaimed()) {
            boolean isGuild = chunkStorage.isGuild();
            GuildStorage guildStorage = null;
            PlayerStorage playerStorage = null;
            if (isGuild)
                guildStorage = new GuildStorage(chunkStorage.getOwner());
            else
                playerStorage = new PlayerStorage(chunkStorage.getOwner());
            int color = ColorUtils.parseColor(isGuild ? guildStorage.getColorString() : ChatColor.WHITE.getName());
            String markup = isGuild ? buildGuildMarkup(guildStorage) : buildLandMarkup(playerStorage);
            String selector = (isGuild ? "guild" : "land") + ".claim." + bitX + ":" + bitZ;

            set.createAreaMarker(selector, markup, true, chunk.getWorld().getName(), xArray, zArray, false);
            set.findAreaMarker(selector).setLineStyle(2, 1, color);
            set.findAreaMarker(selector).setFillStyle(0.3, color);
        }
    }

}