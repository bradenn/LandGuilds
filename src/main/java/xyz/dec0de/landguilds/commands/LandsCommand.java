package xyz.dec0de.landguilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.handlers.LandHandler;
import xyz.dec0de.landguilds.storage.ChunkStorage;

import java.io.IOException;

public class LandsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                help(player);
            } else if (args[0].equalsIgnoreCase("claim")) {
                LandHandler.claim(player);
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                LandHandler.unclaim(player);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("kick")) {
                String username = args[1];

                LandHandler.kick(player, username);
            } else if (args[0].equalsIgnoreCase("add")) {
                String username = args[1];

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toAdd = Bukkit.getPlayer(username);

                    ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(),
                            player.getWorld().getChunkAt(player.getLocation()));
                    if (!chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if (chunkStorage.isGuild()) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed by a guild.");
                        return false;
                    }

                    if (chunkStorage.getRole(player.getUniqueId()) != Role.MEMBER) {
                        if (!chunkStorage.getMembers().contains(toAdd)) {
                            player.sendMessage(ChatColor.GREEN + "You have successfully added "
                                    + toAdd.getName() + " to this chunk.");

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
        } else {
            help(player);
        }
        return false;
    }

    public void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands Help -*-*-*-*-*-");
        player.sendMessage(ChatColor.GRAY + "/l help " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Displays help");
        player.sendMessage(ChatColor.GRAY + "/l claim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Claim a chunk");
        player.sendMessage(ChatColor.GRAY + "/l unclaim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Unclaim a chunk");
        player.sendMessage(ChatColor.GRAY + "/l add [player name]" + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Add a player to your chunk");
        player.sendMessage(ChatColor.GRAY + "/l kick [player name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Kick a player from your chunk");
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands Help -*-*-*-*-*-");
    }
}
