package xyz.dec0de.landguilds.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        PlayerStorage playerStorage = new PlayerStorage(player);
        if (playerStorage.getGuild() != null)
            e.getPlayer().setPlayerListName(playerStorage.getGuild().getTag() + " §r" + player.getDisplayName());
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
            } else if (e.getMessage().charAt(0) == '+') {
                e.setCancelled(true);
                playerStorage.getGuild().getEnemies();
            } else if (playerStorage.getGuild() != null) {

                e.setFormat(e.getFormat().replace("{GUILD}", playerStorage.getGuild().getTag()));
            } else {
                e.setFormat(e.getFormat().replace("{GUILD}", ""));
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

    @EventHandler
    public void onEntityMount(EntityMountEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            Location mountLocation = e.getMount().getLocation();
            ChunkStorage chunkStorage = ChunkStorage.getChunk(mountLocation.getWorld(), mountLocation.getChunk());

            if (chunkStorage.isClaimed()) {
                if (chunkStorage.isGuild()) {
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(Messages.NO_INTERACT.getMessage());
                        e.setCancelled(true);
                    }
                } else {
                    if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(Messages.NO_INTERACT.getMessage());
                        e.setCancelled(true);
                    }
                }

            }
        }
    }
}
