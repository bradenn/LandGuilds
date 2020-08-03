package xyz.dec0de.landguilds.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import xyz.dec0de.landguilds.utils.MenuUtils;

public class GuildGui {

    public static void showMenu(Player p){
        Inventory inv = MenuUtils.createMenu("Guild Menu");
        inv.addItem(MenuUtils.createItem("Tag Color", null, 1, Material.LIGHT_BLUE_DYE));
        inv.addItem(MenuUtils.createItem("Tag Color", null, 1, Material.LIGHT_BLUE_DYE));
      //  p.openInventory(inv);
    }

}
