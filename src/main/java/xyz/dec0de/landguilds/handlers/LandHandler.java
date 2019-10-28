package xyz.dec0de.landguilds.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class LandHandler {

    /**
     * Used for claiming land as a player.
     *
     * @param player The player that will be claiming land.
     */

    public static void claim(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
            return;
        }

        ChunkStorage chunkStorage = new ChunkStorage(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        if (chunkStorage.isClaimed()) {
            player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
            return;
        }

        //TODO check if they are allowed to claim chunks / have enough unused chunk claims

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        try {
            player.sendMessage(Messages.CLAIMED_LAND.getMessage());
            playerStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
            chunkStorage.claim(player.getUniqueId(), false);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for unclaiming land as a player.
     *
     * @param player The player that will be unclaiming land.
     */

    public static void unclaim(Player player) {
        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());

            return;
        }
        ChunkStorage chunkStorage = new ChunkStorage(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        if (!chunkStorage.isClaimed()) {
            player.sendMessage(Messages.NOT_CLAIMED.getMessage());
            return;
        }

        if (!chunkStorage.isGuild()) {
            if (chunkStorage.getMembers().contains(player.getUniqueId())) {
                if (chunkStorage.getRole(player.getUniqueId()) != Role.MEMBER
                        && chunkStorage.getRole(player.getUniqueId()) != null) {
                    player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
                    try {
                        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                        playerStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.unclaim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
                    return;
                }
                return;
            } else {
                player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
            }
        } else {
            player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
            return;
        }
    }

    /**
     * Kick a player from someone's personal claimed land.
     *
     * @param player       The player that is kicking someone from their land.
     * @param targetPlayer Target username of player to kick.
     */

    public static void kick(Player player, String targetPlayer) {
        String username = targetPlayer;

        //TODO get all the players in the current chunk
        // and compare the username that was entered to their cached usernames.

        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toKick = Bukkit.getPlayer(username);

            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(),
                    player.getWorld().getChunkAt(player.getLocation()));
            if (!chunkStorage.isClaimed()) {
                player.sendMessage(Messages.NOT_CLAIMED.getMessage());
                return;
            }

            if (chunkStorage.isGuild()) {
                player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
                return;
            }

            if (chunkStorage.getRole(player.getUniqueId()) != Role.MEMBER
                    && chunkStorage.getRole(player.getUniqueId()) != null) {
                if (chunkStorage.getMembers().contains(toKick.getUniqueId())) {
                    if (username.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Messages.KICK_SELF_FAIL.getMessage());
                        return;
                    }

                    player.sendMessage(Messages.KICK_SELF_FAIL.getMessage(toKick.getName()));

                    try {
                        chunkStorage.removeMember(toKick.getUniqueId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                } else {
                    player.sendMessage(Messages.NOT_IN_LAND_OR_GUILD.getMessage());
                    return;
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                return;
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }
    }

    /**
     * Add targetPlayer to player's claimed chunk
     *
     * @param player       Player of claimed chunk
     * @param targetPlayer Target player to add
     */

    public static void add(Player player, String targetPlayer) {
        String username = targetPlayer;

        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toAdd = Bukkit.getPlayer(username);

            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(),
                    player.getWorld().getChunkAt(player.getLocation()));
            if (!chunkStorage.isClaimed()) {
                player.sendMessage(Messages.NOT_CLAIMED.getMessage());
                return;
            }

            if (chunkStorage.isGuild()) {
                player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
                return;
            }

            if (chunkStorage.getRole(player.getUniqueId()) != Role.MEMBER) {
                if (!chunkStorage.getMembers().contains(toAdd)) {
                    player.sendMessage(Messages.ADD_PLAYER_LAND.getMessage(toAdd.getName()));

                    try {
                        chunkStorage.addMember(toAdd.getUniqueId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                return;
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }
    }
}
