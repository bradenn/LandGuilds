package xyz.dec0de.landguilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class MenuUtils {

    public static Inventory createMenu(String title){
        Inventory inv = Bukkit.createInventory(null, 9, title);
        return inv;
    }

    public static ItemStack createItem(String name, List<String> lore, int count, Material material) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createSkull(String name, List<String> lore, int count, UUID uuid){
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) is.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        sm.setDisplayName(name);
        sm.setLore(lore);
        is.setItemMeta(sm);
        return is;
    }

}
