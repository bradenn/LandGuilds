package xyz.dec0de.landguilds.handlers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.util.*;

public class MapHandler {

    private static char CLAIMED_CHAR = '⬛';
    private static char UNCLAIMED_CHAR = '⬜';
    private static char CENTER_CHAR = '⬤';

    public static void showMap(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
        }

        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        World world = location.getWorld();

        if (Main.allowedWorlds().contains(world.getName())) {

            int centerX = chunk.getX();
            int centerZ = chunk.getZ();

            HashMap<String, ChatColor> landOwners = new HashMap<>();

            int lineNum = 0;
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                String currentLine = "";
                for (int x = centerX - 3; x <= centerX + 3; x++) {

                    Chunk currentChunk = world.getChunkAt(x, z);
                    ChunkStorage chunkStorage = new ChunkStorage(world, currentChunk);

                    char symbol = (x == centerX && z == centerZ) ? CENTER_CHAR : chunkStorage.isClaimed() ? CLAIMED_CHAR : UNCLAIMED_CHAR;

                    if (chunkStorage.isClaimed()) {
                        String owner = chunkStorage.isGuild() ?
                                "GUILD_" + chunkStorage.getOwner().toString()
                                : chunkStorage.getOwner().toString();

                        ChatColor color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(15)));
                        if (!landOwners.containsKey(owner)) {
                            while (landOwners.containsValue(color)) {
                                color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(15)));
                            }
                            landOwners.put(owner, color);
                        } else {
                            color = landOwners.get(owner);
                        }

                        currentLine = currentLine + color + symbol + " ";
                    } else {
                        currentLine = currentLine + ChatColor.WHITE + symbol + " ";
                    }
                }

                switch (lineNum) {
                    case 3:
                        currentLine = currentLine + ChatColor.RESET +
                                ChatColor.GRAY + ChatColor.BOLD
                                + "     " +
                                "   North";
                        break;
                    case 4:
                        currentLine = currentLine + ChatColor.RESET +
                                ChatColor.GRAY + ChatColor.BOLD
                                + "     " +
                                "West + East";
                        break;
                    case 5:
                        currentLine = currentLine + ChatColor.RESET +
                                ChatColor.GRAY + ChatColor.BOLD
                                + "     " +
                                "   South";
                        break;
                }

                player.sendMessage(currentLine);

                lineNum++;
            }

            String landOwnerList = landOwners.entrySet().size() > 0 ?
                    ChatColor.GRAY + "" + ChatColor.ITALIC + "Land Owner" +
                            (landOwners.entrySet().size() > 1 ? "s" : "") + ": "
                            + ChatColor.RESET : "";

            for (Map.Entry<String, ChatColor> land : landOwners.entrySet()) {
                String ownerTag = "";

                if (land.getKey().startsWith("GUILD_")) {
                    GuildStorage guild = new GuildStorage(UUID.fromString(land.getKey().replace("GUILD_", "")));
                    ownerTag = ChatColor.stripColor(guild.getTag());
                } else {
                    PlayerStorage playerStorage = new PlayerStorage(UUID.fromString(land.getKey()));
                    ownerTag = playerStorage.getUsername();
                }


                landOwnerList = landOwnerList + ChatColor.GRAY + land.getValue() + ownerTag + " ";
            }

            player.sendMessage(landOwnerList);
        }
    }

    public static void generateInventoryMap(Player player) {
        Server server = player.getServer();
        Inventory inv = server.createInventory(null, 9 * 5, "Guilds Map");
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        World world = location.getWorld();

        if (Main.allowedWorlds().contains(world.getName())) {

            int centerX = chunk.getX();
            int centerZ = chunk.getZ();

            int clk = 0;
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                for (int x = centerX - 4; x <= centerX + 4; x++) {
                    Chunk currentChunk = world.getChunkAt(x, z);
                    ChunkStorage chunkStorage = new ChunkStorage(world, currentChunk);

                    if (chunkStorage.isClaimed()) {
                        if (chunkStorage.isGuild()) {
                            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                            List<String> players = new ArrayList<>();
                            guildStorage.getMembers().forEach(uuid -> {
                                PlayerStorage playerStorage = new PlayerStorage(uuid);
                                players.add(playerStorage.getUsername());
                            });
                            Material material = (players.contains(player.getName()))?Material.BLUE_STAINED_GLASS_PANE:Material.ORANGE_STAINED_GLASS_PANE;
                            inv.setItem(clk, InventoryHandler.createItemStack(guildStorage.getName(), material, players));
                        } else {
                            PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                            Material material = (playerStorage.getUsername().equalsIgnoreCase(player.getName()))?Material.GREEN_STAINED_GLASS_PANE:Material.RED_STAINED_GLASS_PANE;
                            inv.setItem(clk, InventoryHandler.createItemStack(playerStorage.getUsername(), material, new ArrayList<String>()));

                        }

                    } else {
                        inv.setItem(clk, InventoryHandler.createItemStack("Unclaimed", Material.GRAY_STAINED_GLASS_PANE, new ArrayList<String>()));

                    }
                    clk++;

                }
            }
        }
        player.openInventory(inv);

    }
}
