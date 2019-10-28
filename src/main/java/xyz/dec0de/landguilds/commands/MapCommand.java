package xyz.dec0de.landguilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.handlers.MapHandler;

public class MapCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.MUST_BE_PLAYER.getMessage());
            return false;
        }
        Player player = (Player) sender;

        MapHandler.showMap(player);

        return false;
    }
}