package xyz.dec0de.landguilds.handlers;

import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminHandler {

    /**
     * Admin method to unclaim anyones chunk where player is located
     *
     * @param player Player that will be used to get location of current chunk to unclaim
     */

    public static void unclaim(Player player) {
        ChunkStorage chunkStorage = new ChunkStorage(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        if (!chunkStorage.isClaimed()) {
            player.sendMessage(Messages.NOT_CLAIMED.getMessage());
            return;
        }

        if (!chunkStorage.isGuild()) {
            player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
            try {
                PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                playerStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                chunkStorage.unclaim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        } else {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
            try {
                guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                chunkStorage.unclaim();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }
    }

    private static List<UUID> overrideAdmins = new ArrayList<>();

    public static boolean isOverride(UUID uuid) {
        if (overrideAdmins.contains(uuid)) {
            return true;
        }

        return false;
    }

    public static void addOverride(UUID uuid) {
        if (!isOverride(uuid)) {
            overrideAdmins.add(uuid);
        }
    }

    public static void removeOverride(UUID uuid) {
        if (isOverride(uuid)) {
            overrideAdmins.remove(uuid);
        }
    }
}
