package xyz.dec0de.landguilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Roles;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class LandsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("claim")) {
                if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                    ChunkStorage chunkStorage = new ChunkStorage(
                            player.getWorld(),
                            player.getWorld().getChunkAt(player.getLocation()));

                    if (chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk has already been claimed.");
                    }

                    //TODO check if they are allowed to claim chunks / have enough unused chunk claims

                    PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                    try {
                        player.sendMessage(ChatColor.GREEN + "Successfully claimed a chunk!");
                        playerStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.claim(player.getUniqueId(), false);
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                    ChunkStorage chunkStorage = new ChunkStorage(
                            player.getWorld(),
                            player.getWorld().getChunkAt(player.getLocation()));

                    if (!chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if (!chunkStorage.isGuild()) {
                        if (chunkStorage.getRole(player.getUniqueId()) != Roles.MEMBER && chunkStorage.getRole(player.getUniqueId()) != null) {
                            player.sendMessage(ChatColor.GREEN +
                                    "You have unclaimed this chunk.");
                            try {
                                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                                playerStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                chunkStorage.unclaim();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            player.sendMessage(
                                    ChatColor.RED +
                                            "You do not own this land.");
                            return false;
                        }
                        return false;
                    } else {
                        player.sendMessage(ChatColor.RED + "This land was claimed by a guild.");
                        return false;
                    }
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("kick")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toKick = Bukkit.getPlayer(username);
                    PlayerStorage toKickPlayerStorage = new PlayerStorage(toKick.getUniqueId());

                    ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                    if (!chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if (chunkStorage.isGuild()) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed by a guild.");
                        return false;
                    }

                    if (chunkStorage.getRole(player.getUniqueId()) != Roles.MEMBER && chunkStorage.getRole(player.getUniqueId()) != null) {
                        if (chunkStorage.getMembers().contains(toKick.getUniqueId())) {
                            if (username.equalsIgnoreCase(player.getName())) {
                                player.sendMessage(ChatColor.RED + "You cannot kick yourself.");
                                return false;
                            }

                            player.sendMessage(ChatColor.GREEN + "You have successfully kicked " + toKick.getName() + " from this chunk.");

                            try {
                                chunkStorage.removeMember(toKick.getUniqueId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return false;
                        } else {
                            player.sendMessage(ChatColor.RED + "This player has not been added to this land.");
                            return false;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not the owner of this land.");
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                String username = args[1];

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toAdd = Bukkit.getPlayer(username);

                    ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                    if (!chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if (chunkStorage.isGuild()) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed by a guild.");
                        return false;
                    }

                    if (chunkStorage.getRole(player.getUniqueId()) != Roles.MEMBER) {
                        if (!chunkStorage.getMembers().contains(toAdd)) {
                            player.sendMessage(ChatColor.GREEN + "You have successfully added " + toAdd.getName() + " to this chunk.");

                            try {
                                chunkStorage.addMember(toAdd.getUniqueId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not the owner of this land.");
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            }
        }
        return false;
    }
}
