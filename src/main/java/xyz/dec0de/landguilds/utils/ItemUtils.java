package xyz.dec0de.landguilds.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    /**
     * Create an item
     *
     * @param mat    material of item
     * @param amount amount of item
     * @param name   name of item
     * @param lore   lore...
     */
    public static ItemStack createItem(Material mat, int amount, String name, String... lore) {
        ItemStack is = new ItemStack(mat, amount);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> newLore = new ArrayList<>();
        for (String l : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', l));
        }

        im.setLore(newLore);
        is.setItemMeta(im);
        return is;
    }

    /**
     * Create a colored item
     *
     * @param mat    material
     * @param amount amount of item
     * @param r      red
     * @param g      green
     * @param b      blue
     * @param name   name of item
     * @param lore   lore...
     */
    public static ItemStack createItem(Material mat, int amount, int r, int g, int b, String name, String... lore) {
        ItemStack is = new ItemStack(mat, amount);
        LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> newLore = new ArrayList<>();

        for (String l : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', l));
        }

        im.setLore(newLore);
        im.setColor(Color.fromRGB(r, g, b));

        is.setItemMeta(im);
        return is;
    }

    /**
     * Create an item from itemstack
     *
     * @param i      itemstack
     * @param amount amount of itemstack
     * @param name   name of itemstack
     * @param lore   lore...
     */
    public static ItemStack createItem(ItemStack i, int amount, String name, String... lore) {
        ItemStack is = i;
        is.setAmount(amount);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> newLore = new ArrayList<>();
        for (String l : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', l));
        }

        im.setLore(newLore);
        is.setItemMeta(im);
        return is;
    }

    /**
     * Player skull from uuid
     *
     * @param uuid uuid of player
     */
    public static ItemStack playerSkull(UUID uuid) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGlowing(ItemStack item) {
        return item.getItemMeta().hasEnchants();
    }

    public static void addGlow(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(meta);
    }

    public static ItemStack removeGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(Enchantment.LURE);
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setGlowing(ItemStack item, boolean bool) {
        ItemStack is = item.clone();
        ItemMeta ism = is.getItemMeta();

        if (bool) {
            ism.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(ism);
            is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        } else {
            ism.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(ism);
            is.removeEnchantment(Enchantment.ARROW_DAMAGE);
        }

        return is;
    }

    public static ItemStack createItem(ItemStack item, String name, List<String> lore) {
        ItemStack is = item.clone();
        ItemMeta ism = is.getItemMeta();
        List<String> newLore = new ArrayList<>();
        ism.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        ism.setLore(newLore);
        is.setItemMeta(ism);
        return is;
    }

    public static ItemStack createItem(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(ItemStack item) {
        return item.clone();
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore, String localizedName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        meta.setLocalizedName(localizedName);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(ItemStack item, String name) {
        ItemStack is = item.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createSkullItem(String owner) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createSkullItem(String owner, String displayName) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createSkullItem(String owner, String displayName, List<String> lore) {
        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createSkullItem(ItemStack item, String displayName) {
        ItemStack is = new ItemStack(item);
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(Material material) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(ItemStack item, String displayName) {
        ItemStack is = item.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(ItemStack item, String displayName, List<String> lore) {
        ItemStack is = item.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(ItemStack item, String displayName, List<String> lore, String localizedName) {
        ItemStack is = item.clone();
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        meta.setLocalizedName(localizedName);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(Material material, String displayName) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(Material material, String displayName, List<String> lore) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createItemNoAttrib(Material material, String displayName, List<String> lore, String localizedName) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        meta.setLocalizedName(localizedName);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createDyeableTunicItem(String displayName, int r, int g, int b) {
        ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createDyeableItem(Material material, String displayName, int r, int g, int b) {
        ItemStack is = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createTippedArrow(String name, PotionType potionType) {
        ItemStack is = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setBasePotionData(new PotionData(potionType));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createPotion(String name, PotionType potionType) {
        ItemStack is = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setBasePotionData(new PotionData(potionType));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createPotion(PotionType potionType, String name, List<String> lore) {
        ItemStack is = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setBasePotionData(new PotionData(potionType));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(newLore);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createLingeringPotion(String name, PotionType potionType) {
        ItemStack is = new ItemStack(Material.LINGERING_POTION);
        PotionMeta meta = (PotionMeta) is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setBasePotionData(new PotionData(potionType));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack createDragonBreathPotion(String name) {
        ItemStack is = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack addLocalizedName(ItemStack item, String name) {
        ItemStack is = item.clone();
        ItemMeta meta = item.getItemMeta();

        meta.setLocalizedName(name);

        is.setItemMeta(meta);
        return is;
    }
}