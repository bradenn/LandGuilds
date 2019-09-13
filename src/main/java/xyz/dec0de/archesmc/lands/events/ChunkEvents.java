package xyz.dec0de.archesmc.lands.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.dec0de.archesmc.lands.Main;
import xyz.dec0de.archesmc.lands.storage.ChunkStorage;
import xyz.dec0de.archesmc.lands.storage.GuildStorage;
import xyz.dec0de.archesmc.lands.storage.PlayerStorage;

import java.util.HashMap;
import java.util.UUID;

public class ChunkEvents implements Listener {

    private HashMap<UUID, String> playersInChunk = new HashMap<>();

    //TODO prevent explosions from breaking blocks
    //TODO prevent passive mob killing
    //TODO

    // Chunk entry / exit action bar
    @EventHandler
    public void moveEvent(PlayerMoveEvent e){
        //TODO chunk particles?

        Player player = e.getPlayer();
        if(Main.allowedWorlds().contains(player.getWorld().getName())){
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
            if(chunkStorage.isClaimed()){
                String landOwner;

                if(chunkStorage.isGuild()){
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    landOwner = guildStorage.getTag();
                }else{
                    PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                    landOwner = playerStorage.getUsername();
                }

                if(playersInChunk.containsKey(player.getUniqueId())){
                    if(!playersInChunk.get(player.getUniqueId()).equalsIgnoreCase(landOwner)){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                ChatColor.GREEN + "Entering " + landOwner + ChatColor.GREEN + "'s Land..."));
                        playersInChunk.put(player.getUniqueId(), landOwner);
                    }
                }else{
                    playersInChunk.put(player.getUniqueId(), landOwner);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            ChatColor.GREEN + "Entering " + landOwner + ChatColor.GREEN + "'s Land..."));
                }
            }else{
                if(playersInChunk.containsKey(player.getUniqueId())){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            ChatColor.RED + "Leaving " + playersInChunk.get(player.getUniqueId()) + ChatColor.RED + "'s Land..."));
                    playersInChunk.remove(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e){
        Player player = e.getPlayer();
        if(Main.allowedWorlds().contains(player.getWorld().getName())){
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
            if(chunkStorage.isClaimed()) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if(chunkStorage.isGuild()){
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    if(playerStorage.getGuild().getUuid() == null){
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot break here.");
                        e.setCancelled(true);
                        return;
                    }else{
                        if(playerStorage.getGuild().getUuid() != chunkStorage.getOwner()){
                            player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot break here.");
                            e.setCancelled(true);
                            return;
                        }
                    }
                }

                if(!chunkStorage.getMembers().contains(player.getUniqueId())){
                    player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot break here.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if(Main.allowedWorlds().contains(player.getWorld().getName())){
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
            if(chunkStorage.isClaimed()) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if(chunkStorage.isGuild()){
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    if(playerStorage.getGuild().getUuid() == null){
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot place here.");
                        e.setCancelled(true);
                        return;
                    }else{
                        if(playerStorage.getGuild().getUuid() != chunkStorage.getOwner()){
                            player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot place here.");
                            e.setCancelled(true);
                            return;
                        }
                    }
                }

                if(!chunkStorage.getMembers().contains(player.getUniqueId())){
                    player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot place here.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
