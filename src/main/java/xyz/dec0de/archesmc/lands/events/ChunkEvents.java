package xyz.dec0de.archesmc.lands.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.dec0de.archesmc.lands.Main;
import xyz.dec0de.archesmc.lands.storage.ChunkStorage;
import xyz.dec0de.archesmc.lands.storage.GuildStorage;
import xyz.dec0de.archesmc.lands.storage.PlayerStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChunkEvents implements Listener {

    private HashMap<UUID, String> playersInChunk = new HashMap<>();

    // Chunk entry / exit action bar
    @EventHandler
    public void moveEvent(PlayerMoveEvent e){
        //TODO chunk particles?

        Player player = e.getPlayer();
        if(Main.allowedWorlds().contains(player.getWorld().getName())){
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
            if(chunkStorage.isClaimed()){
                if(playersInChunk.containsKey(player.getUniqueId())){
                    playersInChunk.remove(player.getUniqueId());
                }

                String landOwner = "UNKNOWN";

                if(chunkStorage.isGuild()){
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    landOwner = guildStorage.getName();
                }else{
                    PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                    landOwner = playerStorage.getUsername();
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                        ChatColor.GREEN + "Entering " + landOwner + "'s Land"));

                if(!playersInChunk.containsKey(player.getUniqueId())){
                    playersInChunk.put(player.getUniqueId(), landOwner);
                }
            }else{
                if(playersInChunk.containsKey(player.getUniqueId())){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            ChatColor.RED + "Leaving " + playersInChunk.get(player.getUniqueId()) + "'s Land"));
                    playersInChunk.remove(player.getUniqueId());
                }
            }
        }
    }
}
