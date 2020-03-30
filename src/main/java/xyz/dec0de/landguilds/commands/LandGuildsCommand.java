package xyz.dec0de.landguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.dec0de.landguilds.Main;

public class LandGuildsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        "&a&lLandGuilds &7&ov" + Main.getPlugin().getDescription().getVersion() + " &aby &a&odec0de.xyz"));

        return false;
    }
}