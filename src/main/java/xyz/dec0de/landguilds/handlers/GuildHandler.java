package xyz.dec0de.landguilds.handlers;

import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class GuildHandler {

    private HashMap<UUID, String> pendingGuildInvites = new HashMap<>();

    public static void create(Player player, String guildName) {

    }

    public static void disband(Player player) {

    }

    /**
     * If a player has enough permissions in a guild
     * allow claiming as apart of a guild
     *
     * @param player Player that will be claiming land for a guild
     */

    public static void claim(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
        }

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            ChunkStorage chunkStorage = new ChunkStorage(
                    player.getWorld(),
                    player.getWorld().getChunkAt(player.getLocation()));

            if (chunkStorage.isClaimed()) {
                player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
                return;
            }

            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER &&
                    guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    player.sendMessage(Messages.CLAIMED_LAND_GUILD.getMessage());
                    guildStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                    chunkStorage.claim(guildStorage.getUuid(), true);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
    }

    public static void unclaim(Player player) {

    }

    public static void invite(Player player, String targetPlayer) {

    }

    public static void join(Player player) {

    }

    public static void leave(Player player) {

    }

    public static void kick(Player player, String targetPlayer) {

    }

    public static void promote(Player player, String targetPlayer) {

    }

    public static void demote(Player player, String targetPlayer) {

    }
}
