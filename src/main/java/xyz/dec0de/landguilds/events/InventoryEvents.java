package xyz.dec0de.landguilds.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryEvents implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().equalsIgnoreCase("Guilds Map")){
           e.setCancelled(true);
        }

    }

}
