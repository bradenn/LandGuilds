package xyz.dec0de.landguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class AdminCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You must be an operator to use this command.");
            return false;
        }

        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(ChatColor.RED + "This is not enabled in this world.");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("unclaim")) {
                ChunkStorage chunkStorage = new ChunkStorage(
                        player.getWorld(),
                        player.getWorld().getChunkAt(player.getLocation()));

                if (!chunkStorage.isClaimed()) {
                    player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                    return false;
                }

                if (!chunkStorage.isGuild()) {
                    player.sendMessage(ChatColor.GREEN +
                            "You have unclaimed this chunk.");
                    try {
                        PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                        playerStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.unclaim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                } else {
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    player.sendMessage(ChatColor.GREEN +
                            "You have unclaimed this chunk from your guild.");
                    try {
                        guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.unclaim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return false;
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                help(player);
            }
        }

        return false;
    }

    public void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands&Guilds Admin Help -*-*-*-*-*-");
        player.sendMessage(ChatColor.GRAY + "/lgadmin help " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Displays help");
        player.sendMessage(ChatColor.GRAY + "/lgadmin unclaim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Unclaim anyones chunk");
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands&Guilds Admin Help -*-*-*-*-*-");
    }
}
