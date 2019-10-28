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

    private static HashMap<UUID, String> pendingGuildInvites = new HashMap<>();

    public static void create(Player player, String guildName) {

    }

    /**
     * If a player has enough permissions in a guild,
     * disband that guild and unclaim all land and
     * delete the file for the guild.
     *
     * @param player The player that has enough permissions to disband.
     */

    public static void disband(Player player) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) == Role.OWNER
                    && guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    player.sendMessage(Messages.GUILD_DISBAND_SUCCESS.getMessage());
                    guildStorage.disbandGuild();
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

    /**
     * Unclaim land from a guild if a user
     * has enough permissions within that guild
     *
     * @param player The player that is unclaiming land from a guild
     */
    public static void unclaim(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
        }

        ChunkStorage chunkStorage = new ChunkStorage(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        if (!chunkStorage.isClaimed()) {
            player.sendMessage(Messages.NOT_CLAIMED.getMessage());
            return;
        }

        if (chunkStorage.isGuild()) {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            if (guildStorage.getMembers().contains(player.getUniqueId())) {
                if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER
                        && guildStorage.getRole(player.getUniqueId()) != null) {
                    player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
                    try {
                        guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.unclaim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;

                } else {
                    player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                }
            } else {
                player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
            }
            return;
        } else {
            player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
            return;
        }

    }

    public static void invite(Player player, String targetPlayer) {

    }

    /**
     * Join a guild if you have a pending invite already.
     *
     * @param player The player that is joining a guild
     */

    public static void join(Player player) {
        if (pendingGuildInvites.containsKey(player.getUniqueId())) {
            PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
            if (playerStorage.getGuild() == null) {
                UUID guildUuid = UUID.fromString(pendingGuildInvites.get(
                        player.getUniqueId()).split("\\_")[1]);
                try {
                    playerStorage.setGuild(guildUuid);
                    GuildStorage guildStorage = new GuildStorage(guildUuid);
                    guildStorage.addMember(player.getUniqueId());

                    player.sendMessage(Messages.JOIN_GUILD.getMessage());

                    pendingGuildInvites.remove(player.getUniqueId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.ALREADY_GUILD.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_PENDING_INVITES.getMessage());
            return;
        }
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
