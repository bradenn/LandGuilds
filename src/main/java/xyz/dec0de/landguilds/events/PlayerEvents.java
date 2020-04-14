package xyz.dec0de.landguilds.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.dec0de.landguilds.enums.Tags;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        PlayerStorage playerStorage = new PlayerStorage(player);
        e.getPlayer().setPlayerListName(playerStorage.getGuild().getTag() + " §r" + player.getDisplayName());
    }

    @EventHandler
    public void onFireTick(BlockSpreadEvent e) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getBlock().getWorld(), e.getBlock().getChunk());
        if (chunkStorage.isGuild()) {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            if (!guildStorage.getTag(Tags.FIRESPREAD)) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onFireTick(BlockExplodeEvent e) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getBlock().getWorld(), e.getBlock().getChunk());
        if (chunkStorage.isGuild()) {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            if (!guildStorage.getTag(Tags.BOOM)) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        try {
            PlayerStorage playerStorage = new PlayerStorage(e.getPlayer());
            if (e.getMessage().charAt(0) == '`') {
                e.setCancelled(true);
                playerStorage.getGuild().getMembers().forEach(member -> {
                    Player target = e.getPlayer().getServer().getPlayer(member);
                    if (target != null) {
                        target.sendMessage(e.getPlayer().getDisplayName() + " §c->§r " + playerStorage.getGuild().getColor() + playerStorage.getGuild().getName() + "§8:§r " + e.getMessage().substring(1));
                    }
                });
            } else {
                if (playerStorage.getGuild() != null) {

                    e.setFormat(e.getFormat().replace("{GUILD}", playerStorage.getGuild().getTag()));
                } else {
                    e.setFormat(e.getFormat().replace("{GUILD}", ""));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(e.getDeathMessage() + ", oops.");
    }

    @EventHandler
    public void onRevive(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        player.chat("can i get an f in the chat");
        for (Player p : player.getServer().getOnlinePlayers()) {
            p.chat("f");
        }
    }
}
