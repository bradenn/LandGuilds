package xyz.dec0de.landguilds.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.handlers.GuildHandler;
import xyz.dec0de.landguilds.utils.MenuUtils;

import java.util.HashMap;
import java.util.Map;

public class GuildGui implements Listener {

    public static Inventory inv = MenuUtils.createMenu("Guild Menu");;
    public static Main plugin;

    public static void initializeItems(Main pl) {
        plugin = pl;

    }

    public static void showMenu(Player p) {
        p.openInventory(inv);
    }




}
