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

    @EventHandler
    public void moveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if(Main.allowedWorlds().contains(player.getWorld().getName())){
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
            if(chunkStorage.isClaimed()){
                if(!chunkStorage.isGuild()){
                    PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            ChatColor.GREEN + playerStorage.getUsername() + "'s Land"));
                }

                GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                        ChatColor.GREEN + guildStorage + "'s Land"));

                if(!playersInChunk.contains(player.getUniqueId())){
                    playersInChunk.add(player.getUniqueId());
                }
            }else{
                if(playersInChunk.contains(player.getUniqueId())){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            ChatColor.GREEN + playerStorage.getUsername() + "'s Land"));
                    return;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
            }
        }
    }
}
