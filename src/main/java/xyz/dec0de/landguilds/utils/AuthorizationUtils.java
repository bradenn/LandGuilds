package xyz.dec0de.landguilds.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.enums.AuthorizationLevel;
import xyz.dec0de.landguilds.enums.AuthorizationType;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

public class AuthorizationUtils {

    /**
     * Verify a player's authority
     * @param authorizationType
     * @param player
     * @return false
     */
    public static boolean isAuthorized(AuthorizationType authorizationType,
                                       Player player, Location location){
        switch(authorizationType){
            case INTERACT:
                return authorizeInteract(player, location);
            case GENERAL:
                return authorizeGeneral(player);
            case DESTRUCTIVE:
                return authorizeDestructive(player);
            default:
                break;
        }
        return false;
    }

    /**
     * Authorize player to interact with location
     * @param player
     * @param location
     * @return
     */
    public static boolean authorizeInteract(Player player, Location location){
        // The initialization of chunk storage is not acceptable.
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        ChunkStorage chunkStorage = ChunkStorage.getChunk(location.getWorld(), location.getChunk());
        // Check if the given location chunk is claimed
        if(chunkStorage.isClaimed()){
            if(chunkStorage.isGuild()){
                boolean isMember = chunkStorage.getOwner().equals(playerStorage.getGuild().getUuid());
                boolean isTrusted = new GuildStorage(chunkStorage.getOwner()).getMembers().contains(player.getUniqueId());
                return isMember || isTrusted;
            }else{
                return chunkStorage.getOwner().equals(player.getUniqueId());
            }
        }else{
            return false;
        }
    }

    /**
     *
     * @param player
     * @return
     */
    public static boolean authorizeGeneral(Player player){
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        Role role = playerStorage.getGuild().getRole(player.getUniqueId());
        return  role == Role.OWNER || role == Role.LEADER;
    }

    /**
     *
     * @param player
     * @return
     */
    public static boolean authorizeDestructive(Player player){
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        return playerStorage.getGuild().getRole(player.getUniqueId()) == Role.OWNER;
    }

}
