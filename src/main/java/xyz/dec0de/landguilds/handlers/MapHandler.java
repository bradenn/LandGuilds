package xyz.dec0de.landguilds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MapHandler {

    private static char BLOCK_CHAR = '■';
    private static char STAR_CHAR = '☆';

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

            for (int z = centerZ - 4; z < centerZ + 4; z++) {
                String currentLine = "";
                for (int x = centerX - 4; x < centerX + 4; x++) {

                    Chunk currentChunk = world.getChunkAt(x, z);
                    ChunkStorage chunkStorage = new ChunkStorage(world, currentChunk);

                    if (chunkStorage.isClaimed()) {
                        //check if it is guild, if guild, return GUILD-UUID
                        //if player put player UUID
                        String owner = chunkStorage.isGuild() ?
                                "GUILD_" + chunkStorage.getOwner().toString()
                                : chunkStorage.getOwner().toString();

                        ChatColor color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16)));
                        if (!landOwners.containsKey(owner)) {
                            while (landOwners.containsValue(color)) {
                                color = ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16)));
                            }
                            landOwners.put(owner, color);
                        } else {
                            color = landOwners.get(owner);
                        }

                        if (x == centerX && z == centerZ) {
                            currentLine = currentLine + color + STAR_CHAR + " ";
                        } else {
                            currentLine = currentLine + color + BLOCK_CHAR + " ";
                        }
                    } else {
                        currentLine = currentLine + ChatColor.WHITE + BLOCK_CHAR + " ";
                    }
                }

                player.sendMessage(currentLine);
            }

            String landOwnerList = "";

            for (Map.Entry<String, ChatColor> land : landOwners.entrySet()) {
                String ownerTag = "";

                if (land.getKey().startsWith("GUILD_")) {
                    GuildStorage guild = new GuildStorage(UUID.fromString(land.getKey().replace("GUILD_", "")));
                    ownerTag = guild.getTag();
                } else {
                    PlayerStorage playerStorage = new PlayerStorage(UUID.fromString(land.getKey()));
                    ownerTag = playerStorage.getUsername();
                }


                landOwnerList = landOwnerList + ChatColor.GRAY + land.getValue() + ownerTag;
            }

            player.sendMessage(landOwnerList);
        }
    }
}
