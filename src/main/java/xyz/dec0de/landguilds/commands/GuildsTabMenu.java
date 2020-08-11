package xyz.dec0de.landguilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class GuildsTabMenu implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> items = new ArrayList<>();
            items.add("help");
            items.add("claim");
            items.add("unclaim");
            items.add("join");
            items.add("disband");
            items.add("leave");
            items.add("members");
            return items;
        }
        return null;
    }
}
