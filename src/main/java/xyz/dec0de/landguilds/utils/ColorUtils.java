package xyz.dec0de.landguilds.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtils {

    public static int parseColor(String color) {
        if (color.startsWith("#")) {
            return hex2Rgb(color.toUpperCase()).getRGB();
        } else {
            try {
                return org.bukkit.Color.fromRGB(colorMap.get(ChatColor.of(color.toUpperCase())).getRed(),
                        colorMap.get(ChatColor.of(color.toUpperCase())).getGreen(),
                        colorMap.get(ChatColor.of(color.toUpperCase())).getBlue()).asRGB();
            } catch (Exception e) {
                return org.bukkit.Color.fromRGB(colorMap.get(ChatColor.WHITE).getRed(),
                        colorMap.get(ChatColor.WHITE).getGreen(),
                        colorMap.get(ChatColor.WHITE).getBlue()).asRGB();
            }
        }
    }

    public static java.awt.Color hex2Rgb(String colorStr) {
        return new java.awt.Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    private static Map<ChatColor, ColorSet<Integer, Integer, Integer>> colorMap = new HashMap<>();

    static {
        colorMap.put(ChatColor.BLACK, new ColorSet<>(0, 0, 0));
        colorMap.put(ChatColor.DARK_BLUE, new ColorSet<>(0, 0, 170));
        colorMap.put(ChatColor.DARK_GREEN, new ColorSet<>(0, 170, 0));
        colorMap.put(ChatColor.DARK_AQUA, new ColorSet<>(0, 170, 170));
        colorMap.put(ChatColor.DARK_RED, new ColorSet<>(170, 0, 0));
        colorMap.put(ChatColor.DARK_PURPLE, new ColorSet<>(170, 0, 170));
        colorMap.put(ChatColor.GOLD, new ColorSet<>(255, 170, 0));
        colorMap.put(ChatColor.GRAY, new ColorSet<>(170, 170, 170));
        colorMap.put(ChatColor.DARK_GRAY, new ColorSet<>(85, 85, 85));
        colorMap.put(ChatColor.BLUE, new ColorSet<>(85, 85, 255));
        colorMap.put(ChatColor.GREEN, new ColorSet<>(85, 255, 85));
        colorMap.put(ChatColor.AQUA, new ColorSet<>(85, 255, 255));
        colorMap.put(ChatColor.RED, new ColorSet<>(255, 85, 85));
        colorMap.put(ChatColor.LIGHT_PURPLE, new ColorSet<>(255, 85, 255));
        colorMap.put(ChatColor.YELLOW, new ColorSet<>(255, 255, 85));
        colorMap.put(ChatColor.WHITE, new ColorSet<>(255, 255, 255));
    }

    private static class ColorSet<R, G, B> {
        R red;
        G green;
        B blue;

        ColorSet(R red, G green, B blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public R getRed() {
            return red;
        }

        public G getGreen() {
            return green;
        }

        public B getBlue() {
            return blue;
        }

    }

}
