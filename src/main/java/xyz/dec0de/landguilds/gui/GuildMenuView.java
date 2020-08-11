package xyz.dec0de.landguilds.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import xyz.dec0de.landguilds.handlers.GuildHandler;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;
import xyz.dec0de.landguilds.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildMenuView implements Listener {

    public static void showView(Player p) {
        PlayerStorage ps = new PlayerStorage(p.getUniqueId());
        GuildStorage gs = ps.getGuild();

        Inventory inv = Bukkit.createInventory(null, 9, gs.getName() + "'s Members (" + gs.getMembers().size() + "/8)");
        gs.getMembers().forEach(member -> {
            String memberName = new PlayerStorage(member).getUsername();
            List<String> lore = new ArrayList<>();
            String role = gs.getRole(member).toString();
            lore.add("§7§lRole: §f" + role.substring(0, 1) + role.toLowerCase().substring(1));
            lore.add("§8 ");
            lore.add("§8Click to edit");
            inv.addItem(ItemUtils.createSkullItem(memberName, "§a§l" + memberName, lore));
        });
        p.openInventory(inv);
    }

    public static void showOptions(Player player, String target) {
        PlayerStorage ps = new PlayerStorage(player.getUniqueId());
        GuildStorage gs = ps.getGuild();
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, target);
        List<String> lore = new ArrayList<>();

        UUID guildMember = null;
        for (UUID member : gs.getMembers()) {
            if (new PlayerStorage(member).getUsername().equalsIgnoreCase(target)) {
                guildMember = member;
            }
        }

        String role = gs.getRole(guildMember).toString();
        lore.add("§7§lRole: §f" + role.substring(0, 1) + role.toLowerCase().substring(1));
        lore.add("§8 ");
        lore.add("§8Click to edit");
        inv.setItem(1, ItemUtils.createItem(Material.BARRIER, 1, "§a§lGo Back"));
        inv.setItem(3, ItemUtils.createItem(Material.DIAMOND, 1, "§b§lPromote"));
        inv.setItem(4, ItemUtils.createSkullItem(target, "§a§l" + target, lore));
        inv.setItem(5, ItemUtils.createItem(Material.REDSTONE, 1, "§c§lDemote"));
        inv.setItem(7, ItemUtils.createItem(Material.OAK_DOOR, 1, "§e§lKick"));
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String invTitle = e.getView().getTitle();
        InventoryType invType = e.getInventory().getType();
        if ((e.getInventory().getSize() != 9 && invType != InventoryType.DROPPER) || invTitle.equals("Dropper") || invTitle.equals("Dispenser")) return;
        e.setCancelled(true);
        Player p = ((Player) e.getWhoClicked());
        switch(e.getCurrentItem().getType()){
            case BARRIER:
                showView(p);
                break;
            case PLAYER_HEAD:
                String targetPlayer = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                showOptions(p, targetPlayer);
                break;
            case DIAMOND:
                GuildHandler.promote(p, e.getView().getTitle());
                break;
            case REDSTONE:
                GuildHandler.demote(p, e.getView().getTitle());
                break;
            case OAK_DOOR:
                GuildHandler.kick(p, e.getView().getTitle());
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        String invTitle = e.getView().getTitle();
        InventoryType invType = e.getInventory().getType();
        if ((e.getInventory().getSize() != 9 && invType != InventoryType.DROPPER) || invTitle.equals("Dropper") || invTitle.equals("Dispenser")) return;
        e.setCancelled(true);
    }


}
