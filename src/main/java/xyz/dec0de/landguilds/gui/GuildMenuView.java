package xyz.dec0de.landguilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.dec0de.landguilds.handlers.GuildHandler;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;
import xyz.dec0de.landguilds.utils.MenuUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildMenuView implements Listener {

    public static void showView(Player p) {
        PlayerStorage ps = new PlayerStorage(p.getUniqueId());
        GuildStorage gs = ps.getGuild();

        Inventory inv = Bukkit.createInventory(null, 9, gs.getName() + "'s Members (" + gs.getMembers().size() + "/8)");
        gs.getMembers().forEach(member -> {
            List<String> lore = new ArrayList<>();
            String role = gs.getRole(member).toString();
            lore.add("§7§lRole: §f" + role.substring(0, 1) + role.toLowerCase().substring(1));
            lore.add("§8 ");
            lore.add("§8Click to edit");
            inv.addItem(MenuUtils.createSkull("§a§l" + Bukkit.getOfflinePlayer(member).getName(), lore, 1, member));
        });
        p.openInventory(inv);
    }

    public static void showOptions(Player player, Player target) {
        PlayerStorage ps = new PlayerStorage(player.getUniqueId());
        GuildStorage gs = ps.getGuild();
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, target.getName());
        List<String> lore = new ArrayList<>();
        String role = gs.getRole(target.getUniqueId()).toString();
        lore.add("§7§lRole: §f" + role.substring(0, 1) + role.toLowerCase().substring(1));
        lore.add("§8 ");
        lore.add("§8Click to edit");
        inv.setItem(1, MenuUtils.createItem("§a§lGo Back", null, 1, Material.BARRIER));
        inv.setItem(3, MenuUtils.createItem("§b§lPromote", null, 1, Material.DIAMOND));
        inv.setItem(4, MenuUtils.createSkull("§a§l" + target.getName(), lore, 1, target.getUniqueId()));
        inv.setItem(5, MenuUtils.createItem("§c§lDemote", null, 1, Material.REDSTONE));
        inv.setItem(7, MenuUtils.createItem("§e§lKick", null, 1, Material.OAK_DOOR));
        player.openInventory(inv);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if ((e.getInventory().getSize() != 9 && e.getInventory().getType() != InventoryType.DROPPER) || e.getView().getTitle().equals("Dropper")) return;
        e.setCancelled(true);
        Player p = ((Player) e.getWhoClicked());
        if (e.getCurrentItem().getType() == Material.BARRIER) {
            // Handler Back
            p.closeInventory();
            showView(p);
        } else if (e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            // Handler player selection
            SkullMeta sm = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player targetPlayer = sm.getOwningPlayer().getPlayer();
            showOptions(p, targetPlayer);
        } else if (e.getCurrentItem().getType() == Material.DIAMOND) {
            p.closeInventory();
            GuildHandler.promote(p, e.getView().getTitle());
        } else if (e.getCurrentItem().getType() == Material.REDSTONE) {
            p.closeInventory();
            GuildHandler.demote(p, e.getView().getTitle());
        } else if (e.getCurrentItem().getType() == Material.OAK_DOOR) {
            p.closeInventory();
            GuildHandler.kick(p, e.getView().getTitle());
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getSize() != 9 && e.getInventory().getType() != InventoryType.DROPPER) return;
        e.setCancelled(true);
    }


}
