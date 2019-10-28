package xyz.dec0de.landguilds.enums;

import org.bukkit.ChatColor;

public enum Messages {

    INVALID_WORLD("&cYou are in an invalid world and cannot execute this command."),
    NO_GUILD("&cYou are not apart of a guild. Please join one, or create one."),
    CLAIMED_LAND_GUILD("&aYou have successfully claimed land for your guild."),
    UNCLAIM_SUCCESS("&aYou have unclaimed this chunk."),
    CLAIMED_LAND("&aYou have successfully claimed personal land."),
    NOT_CLAIMED("&cThis chunk is not claimed."),
    LAND_NOT_OWNER("&cYou do not own this land."),
    NO_PERMISSIONS("&cYou do not have enough permissions."),
    MUST_BE_PLAYER("&cYou must be a player to run this command"),
    NO_INTERACT("&cYou cannot interact with this."),
    NO_GUILD_PVP("&cPvP is disabled in this land."),
    NO_BUILD("&cYou cannot build in this land."),
    NO_BREAK("&cYou cannot break in this land."),
    ALREADY_CLAIMED("&cThis land has already been claimed"),
    UNABLE_FIND_PLAYER("&cUnable to find this player. Make sure they are online."),
    GUILD_DEMOTE_MEMBER_FAIL("&c%player% cannot be demoted."),
    GUILD_PROMOTE_FAIL("&c%player% cannot be demoted."),
    GUILD_PROMOTE_SUCCESS("&a%player% has been promoted to leader in the guild."),
    GUILD_DEMOTE_SUCCESS("&a% has been demoted to member in the guild."),
    KICK_SELF_FAIL("&cYou cannot kick yourself."),
    KICK_PLAYER("&aYou have successfully kicked %player%."),
    PLAYER_NOT_IN_LAND_OR_GUILD("&aPlayer is not in land/guild."),
    ADD_PLAYER_LAND("&aYou have successfully added %player%.");

    //todo add more messages

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', message)
                .replace("%player%", playerName);
    }
}
