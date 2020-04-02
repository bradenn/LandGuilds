package xyz.dec0de.landguilds.enums;

import org.bukkit.ChatColor;

public enum Messages {
    PREFIX("&8[&aLandGuilds&8] &r"),
    INVALID_WORLD("&cYou are in an invalid world and cannot execute this command."),
    NO_GUILD("&cYou are not apart of a guild. Please join one, or create one."),
    ALREADY_GUILD("&cYou are already in a guild. Please leave or disband it."),
    CLAIMED_LAND_GUILD("&aYou have successfully claimed land for your guild."),
    UNCLAIM_SUCCESS("&aYou have unclaimed this chunk."),
    CLAIMED_LAND("&aYou have successfully claimed personal land."),
    NOT_CLAIMED("&cThis chunk is not claimed."),
    LAND_NOT_OWNER("&cYou do not own this land."),
    NO_PERMISSIONS("&cYou do not have enough permissions."),
    MUST_BE_PLAYER("&cYou must be a player to run this command."),
    NO_INTERACT("&cYou cannot interact with this."),
    NO_GUILD_PVP("&cPvP is disabled in this land."),
    NO_BUILD("&cYou cannot build in this land."),
    NO_BREAK("&cYou cannot break in this land."),
    NO_KILL_ANIMAL("&cYou cannot kill animals in land you do not own."),
    ALREADY_CLAIMED("&cThis land has already been claimed"),
    UNABLE_FIND_PLAYER("&cUnable to find this player. Make sure they are online."),
    GUILD_DEMOTE_FAIL("&c%player% cannot be demoted."),
    GUILD_PROMOTE_FAIL("&c%player% cannot be promoted."),
    GUILD_PROMOTE_SUCCESS("&a%player% has been promoted to leader in the guild."),
    GUILD_DEMOTE_SUCCESS("&a% has been demoted to member in the guild."),
    KICK_SELF_FAIL("&cYou cannot kick yourself."),
    KICK_PLAYER("&aYou have successfully kicked %player%."),
    NOT_IN_LAND_OR_GUILD("&aPlayer is not in land/guild."),
    ADD_PLAYER_LAND("&aYou have successfully added %player%."),
    NO_PENDING_INVITES("&cYou do not have any pending invites."),
    JOIN_GUILD("&aYou have successfully joined a guild."),
    GUILD_DISBAND_SUCCESS("&aYou have successfully disbanded the guild."),
    ALREADY_PENDING_INVITE("&cThis player already has a pending invite. Please try later."),
    INVITE_EXPIRE("&cThe invite has expired!"),
    INVITE_SENT("&aYou have invited %player% to join your guild."),
    INVITE_NOTIFY("&aYou have been invited to join %args0%. &aExpires in 30 seconds. Type /guilds join"),
    GUILD_LEAVE("&aYou have left your current guild."),
    GUILD_LEAVE_OWNER("&cYou own this guild. You cannot leave it. You must disband or transfer it."),
    ADMIN_OVERRIDE("&aYou have %args0% admin override.");

    //todo add more messages

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', PREFIX.message + message);
    }

    public String getMessage(String playerName) {
        return ChatColor.translateAlternateColorCodes('&', PREFIX.message + message)
                .replace("%player%", playerName);
    }

    public String getMessage(String... args) {
        String msg = ChatColor.translateAlternateColorCodes('&', PREFIX.message + message);
        for (int i = 0; i < args.length; i++) {
            msg.replace("%args" + i + "%", args[i]);
        }

        return msg;
    }

    public String getMessageWithoutColor() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
    }
}
