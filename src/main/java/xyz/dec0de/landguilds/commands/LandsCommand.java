package xyz.dec0de.landguilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                    if(chunkStorage.isClaimed()){
                        player.sendMessage(ChatColor.RED + "This chunk has already been claimed.");
                    }

                    //TODO check if they are allowed to claim chunks / have enough unused chunk claims

                    PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                    try {
                        player.sendMessage(ChatColor.GREEN + "Successfully claimed a chunk!");
                        playerStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.claim(player.getUniqueId(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if (args[0].equalsIgnoreCase("unclaim")) {
                if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                    ChunkStorage chunkStorage = new ChunkStorage(
                            player.getWorld(),
                            player.getWorld().getChunkAt(player.getLocation()));

                    if(!chunkStorage.isClaimed()){
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if(!chunkStorage.isGuild()){
                        if(chunkStorage.getOwner() == player.getUniqueId()) {
                            player.sendMessage(ChatColor.GREEN +
                                    "You have unclaimed this chunk from your guild.");
                            try {
                                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                                playerStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                chunkStorage.unclaim();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            player.sendMessage(
                                    ChatColor.RED +
                                    "You do not have enough permissions " +
                                    "in your guild to do this.");
                        }
                        return false;
                    }else{
                        player.sendMessage(ChatColor.RED + "This land was claimed by a guild.");
                    }
                }
            }
        }else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("kick")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());

                if (Bukkit.getServer().getPlayer(username).isOnline()) {
                    Player toKick = Bukkit.getPlayer(username);
                    PlayerStorage toKickPlayerStorage = new PlayerStorage(toKick.getUniqueId());

                    ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                    if(!chunkStorage.isClaimed()){
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if(chunkStorage.isGuild()){
                        player.sendMessage(ChatColor.RED + "This chunk is claimed by a guild.");
                        return false;
                    }

                    if(chunkStorage.getOwner() == player.getUniqueId()){
                        if(chunkStorage.getMembers().contains(toKick)){
                            player.sendMessage(ChatColor.GREEN + "You have successfully kicked " + toKick.getName() + " from this chunk.");

                            try {
                                chunkStorage.removeMember(toKick.getUniqueId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "You are not the owner of this land.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            }else if (args[0].equalsIgnoreCase("add")) {
                String username = args[1];

                if (Bukkit.getServer().getPlayer(username).isOnline()) {
                    Player toAdd = Bukkit.getPlayer(username);

                    ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                    if(!chunkStorage.isClaimed()){
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if(chunkStorage.isGuild()){
                        player.sendMessage(ChatColor.RED + "This chunk is claimed by a guild.");
                        return false;
                    }

                    if(chunkStorage.getOwner() == player.getUniqueId()){
                        if(!chunkStorage.getMembers().contains(toAdd)){
                            player.sendMessage(ChatColor.GREEN + "You have successfully added " + toAdd.getName() + " to this chunk.");

                            try {
                                chunkStorage.addMember(toAdd.getUniqueId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "You are not the owner of this land.");
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
