package xyz.dec0de.archesmc.lands.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.archesmc.lands.Main;
import xyz.dec0de.archesmc.lands.storage.ChunkStorage;
import xyz.dec0de.archesmc.lands.storage.GuildStorage;
import xyz.dec0de.archesmc.lands.storage.PlayerStorage;

import java.io.IOException;

public class GuildsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
                if (args[0].equalsIgnoreCase("claim")) {
                    PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                    if (playerStorage.getGuild() != null) {
                    if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                        ChunkStorage chunkStorage = new ChunkStorage(
                                player.getWorld(),
                                player.getWorld().getChunkAt(player.getLocation()));

                        if (chunkStorage.isClaimed()) {
                            player.sendMessage(ChatColor.RED + "This chunk has already been claimed.");
                        }

                        GuildStorage guildStorage = playerStorage.getGuild();

                        //TODO check if they have the proper role in their guild

                        try {
                            player.sendMessage(ChatColor.GREEN + "Successfully claimed a chunk!");
                            guildStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                            chunkStorage.claim(guildStorage.getUuid(), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    }else{
                        player.sendMessage(ChatColor.RED + "You are not apart of a guild. Please create or join one.");
                    }
                }
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("create")){
                String name = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if(playerStorage.getGuild() != null){
                    player.sendMessage(ChatColor.RED + "You are already apart of a guild, you must leave or disband your current one.");
                    return false;
                }

                if(name.length() > 6){
                    player.sendMessage(ChatColor.RED + "Your guild name cannot be longer than 6 characters.");
                    return false;
                }

                try {
                    GuildStorage guildStorage = new GuildStorage(name, player.getUniqueId());
                    playerStorage.setGuild(guildStorage.getUuid());

                    player.sendMessage(ChatColor.GREEN + "You have created the guild named" + name + ".");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
