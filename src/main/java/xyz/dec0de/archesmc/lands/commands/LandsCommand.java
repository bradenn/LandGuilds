package xyz.dec0de.archesmc.lands.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.archesmc.lands.Main;
import xyz.dec0de.archesmc.lands.storage.ChunkStorage;
import xyz.dec0de.archesmc.lands.storage.PlayerStorage;

import java.io.IOException;

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
            }
        }
        return false;
    }
}
