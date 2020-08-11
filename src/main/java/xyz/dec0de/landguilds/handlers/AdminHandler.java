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

    public static void info(Player player) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        player.sendMessage("§8[§aLandGuilds§8] §aLand Claim info");
        player.sendMessage("§aClaim status: §7" + ((chunkStorage.isClaimed()) ? "Claimed" : "Unclaimed"));
        if (chunkStorage.isClaimed()) {
        player.sendMessage("§aClaim type: §7" + ((chunkStorage.isGuild()) ? "Guild" : "Land"));
            if (chunkStorage.isGuild()) {
                GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                player.sendMessage("§aGuild: §7" + guildStorage.getTag());
                ArrayList<String> people = new ArrayList<>();
                for(UUID puuid : guildStorage.getMembers()){
                    PlayerStorage pus = new PlayerStorage(puuid);
                    people.add("§7[" + guildStorage.getRole(puuid).toString().toLowerCase() + "] §a" + pus.getUsername());
                }
                player.sendMessage("§aMembers: §7" + String.join(", ", people));
            } else {
                PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                player.sendMessage("§aLand Owner: §7" + playerStorage.getUsername());
            }
        } else {
            player.sendMessage(Messages.NOT_CLAIMED.getMessage());
        }
    }

    /**
     * Admin method to unclaim anyones chunk where player is located
     *
     * @param player Player that will be used to get location of current chunk to unclaim
     */

    public static void unclaim(Player player) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(
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
        } else {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
            try {
                guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                chunkStorage.unclaim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<UUID> overrideAdmins = new ArrayList<>();

    public static boolean isOverride(UUID uuid) {
        return overrideAdmins.contains(uuid);
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
