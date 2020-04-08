package xyz.dec0de.landguilds.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();

        PlayerStorage playerStorage = new PlayerStorage(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        try {
            PlayerStorage playerStorage = new PlayerStorage(e.getPlayer());
            if (e.getMessage().charAt(0) == '`') {
                e.setCancelled(true);
                playerStorage.getGuild().getMembers().forEach(member -> {
                    Player target = e.getPlayer().getServer().getPlayer(member);
                    if(target != null){
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
}
