package xyz.dec0de.landguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.handlers.LandHandler;

public class LandsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_PLAYER.getMessage());
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
                LandHandler.add(player, username);
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
