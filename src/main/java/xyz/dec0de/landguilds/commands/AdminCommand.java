package xyz.dec0de.landguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.handlers.AdminHandler;

public class AdminCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_PLAYER.getMessage());
            return false;
        }

        Player player = (Player) sender;

        if (!player.isOp() || !player.hasPermission("landguilds.admin")) {
            player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            return false;
        }

        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                help(player);
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                AdminHandler.unclaim(player);
            } else if (args[0].equalsIgnoreCase("override")) {
                if (!AdminHandler.isOverride(player.getUniqueId())) {
                    player.sendMessage(Messages.ADMIN_OVERRIDE.getMessage("&2&lEnabled &a"));
                    AdminHandler.addOverride(player.getUniqueId());
                } else {
                    player.sendMessage(Messages.ADMIN_OVERRIDE.getMessage("&c&lDisabled &a"));
                    AdminHandler.removeOverride(player.getUniqueId());
                }
            } else {
                help(player);
            }
        } else {
            help(player);
        }

        return false;
    }

    public void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands&Guilds Admin Help -*-*-*-*-*-");
        player.sendMessage(ChatColor.GRAY + "/lgadmin help " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Displays help");
        player.sendMessage(ChatColor.GRAY + "/lgadmin unclaim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Unclaim anyones chunk");
        player.sendMessage(ChatColor.GRAY + "/lgadmin override " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Override interact/build/break");
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Lands&Guilds Admin Help -*-*-*-*-*-");
    }
}
