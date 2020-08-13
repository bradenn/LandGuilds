package xyz.dec0de.landguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.gui.GuildMenuView;
import xyz.dec0de.landguilds.handlers.GuildHandler;

public class GuildsCommand implements CommandExecutor {


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
                GuildHandler.claim(player);
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                GuildHandler.unclaim(player);
            } else if (args[0].equalsIgnoreCase("join")) {
                GuildHandler.join(player);
            } else if (args[0].equalsIgnoreCase("disband")) {
                GuildHandler.disband(player);
            } else if (args[0].equalsIgnoreCase("leave")) {
                GuildHandler.leave(player);
            } else if (args[0].equalsIgnoreCase("members")) {
                GuildMenuView.showView(player);
            }else{
                help(player);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                GuildHandler.create(player, name);
            } else if (args[0].equalsIgnoreCase("invite")) {
                String username = args[1];
                GuildHandler.invite(player, username);
            } else if (args[0].equalsIgnoreCase("get")) {
                GuildHandler.get(player, args[1]);
            } else if (args[0].equalsIgnoreCase("rename")) {
                GuildHandler.rename(player, args[1]);
            } else if (args[0].equalsIgnoreCase("trust")) {
                GuildHandler.trust(player, args[1]);
            } else if (args[0].equalsIgnoreCase("untrust")) {
                GuildHandler.untrust(player, args[1]);
            } else if (args[0].equalsIgnoreCase("color")) {
                GuildHandler.setColor(player, args[1]);
            } else {
                help(player);
            }


        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("declare")) {
                String targetUser = args[1];
                GuildHandler.setRelationship(player, targetUser, args[2]);
            } else if(args[0].equalsIgnoreCase("set")) {
                GuildHandler.setTag(player, args[1], Boolean.parseBoolean(args[2]));
            }else{
                help(player);
            }
        } else {
            help(player);
        }
        return false;
    }

    public void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Guilds Help -*-*-*-*-*-");
        player.sendMessage(ChatColor.GRAY + "/g help " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Displays help");
        player.sendMessage(ChatColor.GRAY + "/g create [name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Create a guild");
        player.sendMessage(ChatColor.GRAY + "/g disband [name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Disband your guild");
        player.sendMessage(ChatColor.GRAY + "/g claim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Claim a chunk for your guild");
        player.sendMessage(ChatColor.GRAY + "/g join " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Join a guild if you have an invite");
        player.sendMessage(ChatColor.GRAY + "/g leave " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Leave your current guild");
        player.sendMessage(ChatColor.GRAY + "/g unclaim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Unclaim a chunk from your guild");
        player.sendMessage(ChatColor.GRAY + "/g invite [player name]" + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Invite a player to your guild");
        player.sendMessage(ChatColor.GRAY + "/g declare [player name] [ENEMY | ALLY | NEUTRAL]" + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Sets relationship with other guild");
        player.sendMessage(ChatColor.GRAY + "/g set [tag] [true | false] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " change settings (firespread, pvp, boom)");
        player.sendMessage(ChatColor.GRAY + "/g rename [name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Pretty simple; hard to fuck up.");
        player.sendMessage(ChatColor.GRAY + "/g color [color] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Minecraft Colors (IN CAPS) or HEX");
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Guilds Help -*-*-*-*-*-");
    }

}
