package xyz.dec0de.landguilds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;

import java.util.HashMap;
import java.util.Random;

public class MapHandler {

    private static char BLOCK_CHAR = '■';

    public static void showMap(Player player) {
        /*
           Get the current chunk a player is in, that will be the center of the map
           that will be displayed in the chat

           * X increases East, decreases West
           * Z increases South, decreases North

           - unclaimed chunks will be displayed as "■" in gray
           - for each claimed land it will be displayed in a different color
           if it's not the same owner

           - Current position will be displayed as "+" with the color
           being based on the claimed land owner

         */

        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        World world = location.getWorld();

        if (Main.allowedWorlds().contains(world.getName())) {

            int centerX = chunk.getX();
            int centerZ = chunk.getZ();

            HashMap<String, ChatColor> landOwners = new HashMap<>();

            for (int z = centerZ - 7; z < centerZ + 7; z++) {
                String currentLine = "";
                for (int x = centerX - 7; x < centerX + 7; x++) {

                    Chunk currentChunk = world.getChunkAt(x, z);
                    ChunkStorage chunkStorage = new ChunkStorage(world, chunk);

                    if (chunkStorage.isClaimed()) {
                        //check if land is claimed
                        ChatColor color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16)));
                        while (landOwners.containsValue(color)) {
                            color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16)));
                        }

                        if (x != centerX && z != centerZ) {

                        } else {
                            currentLine = currentLine;
                        }
                    } else {
                        currentLine = currentLine + ChatColor.WHITE + BLOCK_CHAR;
                    }
                }
            }
        }
    }
}
