package xyz.dec0de.landguilds.handlers;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.enums.Relationship;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.enums.Tags;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildHandler {

    private static HashMap<UUID, String> pendingGuildInvites = new HashMap<>();

    /**
     * Create a guild, and set player as the owner
     *
     * @param player    Player that is making the guild
     * @param guildName Guild name / prefix
     */

    public static void create(Player player, String guildName) {
        String name = guildName;

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            player.sendMessage(Messages.ALREADY_GUILD.getMessage());
            return;
        }

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);

        if (m.find()) {
            player.sendMessage(ChatColor.RED + "You cannot have any special characters in your guild name.");
            return;
        }

        if (name.length() > 6) {
            player.sendMessage(ChatColor.RED + "Your guild name cannot be longer than 6 characters.");
            return;
        }

        try {
            GuildStorage guildStorage = new GuildStorage(name, player.getUniqueId());
            playerStorage.setGuild(guildStorage.getUuid());

            player.sendMessage(ChatColor.GREEN + "You have created the guild named " + name + ".");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * If a player has enough permissions in a guild,
     * disband that guild and unclaim all land and
     * delete the file for the guild.
     *
     * @param player The player that has enough permissions to disband.
     */

    public static void disband(Player player) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) == Role.OWNER
                    && guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    player.sendMessage(Messages.GUILD_DISBAND_SUCCESS.getMessage());
                    guildStorage.disbandGuild();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
    }

    public static void setColor(Player player, String color) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) == Role.OWNER
                    && guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    if ((color.contains("#") && color.length() == 7) || org.bukkit.ChatColor.valueOf(color.toUpperCase()).isColor()) {
                        guildStorage.setColor((color.contains("#"))?color:color.toUpperCase());
                        player.sendMessage(Messages.COLORED.getMessage("" + ((color.startsWith("#"))?ChatColor.of(color):org.bukkit.ChatColor.valueOf(color).asBungee()) + color));
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IOException | IllegalArgumentException e) {
                    player.sendMessage(Messages.NOT_COLORED.getMessage(color));
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
        }
    }

    /**
     * If a player has enough permissions in a guild
     * allow claiming as apart of a guild
     *
     * @param player Player that will be claiming land for a guild
     */

    public static void claim(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
            return;
        }

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            ChunkStorage chunkStorage = ChunkStorage.getChunk(
                    player.getWorld(),
                    player.getWorld().getChunkAt(player.getLocation()));

            if (chunkStorage.isClaimed()) {
                player.sendMessage(Messages.ALREADY_CLAIMED.getMessage());
                return;
            }

            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER &&
                    guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    player.sendMessage(Messages.CLAIMED_LAND_GUILD.getMessage());
                    guildStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                    chunkStorage.claim(guildStorage.getUuid(), true);
                    new DynmapHandler().reloadChunk(chunkStorage.getChunk());
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
    }

    /**
     * Unclaim land from a guild if a user
     * has enough permissions within that guild
     *
     * @param player The player that is unclaiming land from a guild
     */
    public static void unclaim(Player player) {
        if (!Main.allowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Messages.INVALID_WORLD.getMessage());
            return;
        }

        ChunkStorage chunkStorage = ChunkStorage.getChunk(
                player.getWorld(),
                player.getWorld().getChunkAt(player.getLocation()));

        if (!chunkStorage.isClaimed()) {
            player.sendMessage(Messages.NOT_CLAIMED.getMessage());
            return;
        }

        if (chunkStorage.isGuild()) {
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            if (guildStorage.getMembers().contains(player.getUniqueId())) {
                if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER
                        && guildStorage.getRole(player.getUniqueId()) != null) {
                    player.sendMessage(Messages.UNCLAIM_SUCCESS.getMessage());
                    try {
                        guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                        chunkStorage.unclaim();
                        new DynmapHandler().reloadChunk(chunkStorage.getChunk());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;

                } else {
                    player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                }
            } else {
                player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
            }
            return;
        } else {
            player.sendMessage(Messages.LAND_NOT_OWNER.getMessage());
            return;
        }

    }

    /**
     * Have a player invite someone to their guild.
     *
     * @param player       The player that invites someone to their guild if they have enough permissions.
     * @param targetPlayer The player that is being invited to the guild.
     */

    public static void invite(Player player, String targetPlayer) {
        String username = targetPlayer;

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() == null) {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toInvite = Bukkit.getPlayer(username);
            PlayerStorage invitePlayerStorage = new PlayerStorage(toInvite.getUniqueId());

            if (pendingGuildInvites.containsKey(toInvite.getUniqueId())) {
                player.sendMessage(Messages.ALREADY_PENDING_INVITE.getMessage());
                return;
            }


            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER
                    && guildStorage.getRole(player.getUniqueId()) != null) {
                String guildToken = guildStorage.getName() + "_" + guildStorage.getUuid().toString();
                pendingGuildInvites.put(toInvite.getUniqueId(), guildToken);

                player.sendMessage(Messages.INVITE_SENT.getMessage(toInvite.getName()));

                toInvite.sendMessage(Messages.INVITE_NOTIFY.getMessage(guildStorage.getTag()));

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    public void run() {
                        if (pendingGuildInvites.containsKey(toInvite.getUniqueId()) &&
                                pendingGuildInvites.get(toInvite.getUniqueId()) == guildToken) {
                            player.sendMessage(Messages.INVITE_EXPIRE.getMessage());
                            toInvite.sendMessage(Messages.INVITE_EXPIRE.getMessage());
                            pendingGuildInvites.remove(toInvite.getUniqueId());
                        }
                    }
                }, 30 * 20L); // 20 ticks = 1 second. So 100 ticks = 5 seconds.
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }
    }

    /**
     * Join a guild if you have a pending invite already.
     *
     * @param player The player that is joining a guild
     */

    public static void join(Player player) {
        if (pendingGuildInvites.containsKey(player.getUniqueId())) {
            PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
            if (playerStorage.getGuild() == null) {
                UUID guildUuid = UUID.fromString(pendingGuildInvites.get(
                        player.getUniqueId()).split("\\_")[1]);
                try {
                    playerStorage.setGuild(guildUuid);
                    GuildStorage guildStorage = new GuildStorage(guildUuid);
                    guildStorage.addMember(player.getUniqueId());

                    player.sendMessage(Messages.JOIN_GUILD.getMessage());

                    //TODO message all guild members saying that they joined

                    pendingGuildInvites.remove(player.getUniqueId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.ALREADY_GUILD.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_PENDING_INVITES.getMessage());
            return;
        }
    }

    /**
     * Player leave their current guild
     *
     * @param player The player to leave their current guild
     */

    public static void leave(Player player) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.OWNER
                    && guildStorage.getRole(player.getUniqueId()) != null) {
                try {
                    player.sendMessage(Messages.GUILD_LEAVE.getMessage());
                    playerStorage.removeGuild();
                    guildStorage.removeMember(player.getUniqueId());
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.GUILD_LEAVE_OWNER.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
    }

    /**
     * Kick a player from the guild
     *
     * @param player       Player that has enough permissions who is kicking someone from the guild
     * @param targetPlayer The target player that is being kicked
     */

    public static void kick(Player player, String targetPlayer) {
        String username = targetPlayer;

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() == null) {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }

        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toKick = Bukkit.getPlayer(username);
            PlayerStorage toKickPlayerStorage = new PlayerStorage(toKick.getUniqueId());

            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                if (guildStorage.getMembers().contains(toKick.getUniqueId())) {
                    if (username.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Messages.KICK_SELF_FAIL.getMessage());
                        return;
                    }

                    if (guildStorage.getOwner() == toKick.getUniqueId()) {
                        player.sendMessage(Messages.GUILD_LEAVE_OWNER.getMessage());
                        return;
                    }

                    player.sendMessage(Messages.KICK_PLAYER.getMessage(toKick.getName()));
                    try {
                        toKickPlayerStorage.removeGuild();
                        guildStorage.removeMember(toKick.getUniqueId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(Messages.NOT_IN_LAND_OR_GUILD.getMessage());
                    return;
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                return;
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }

    }

    /**
     * SO the thing is.. You put in a guild name, we parse it, we declare ir, we do it.
     */

    public static void get(Player player, String tag) {

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        player.sendMessage(Messages.GET_TAG.getMessage(tag, playerStorage.getGuild().get(Tags.valueOf(tag.toUpperCase())) + ""));
    }

    public static void setTag(Player player, String tag, boolean option) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        playerStorage.getGuild().setTag(Tags.valueOf(tag.toUpperCase()), option);
        player.sendMessage(Messages.SET_TAG.getMessage(tag, option + ""));
    }

    public static Relationship getRelationship(Player player, UUID uuid) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        return playerStorage.getGuild().getRelationship(uuid);
    }

    public static void setRelationship(Player player, String playerName, String _relationship) {
        if (player.getName().equalsIgnoreCase(playerName)) {
            player.sendMessage(Messages.WAR_ON_THE_HOME_FRONT.getMessage(_relationship));
            return;
        }
        Relationship relationship = Relationship.NEUTRAL;
        if (_relationship.equalsIgnoreCase(Relationship.ALLY.toString())) {
            relationship = Relationship.ALLY;
        } else if (_relationship.equalsIgnoreCase(Relationship.ENEMY.toString())) {
            relationship = Relationship.ENEMY;
        } else if (_relationship.equalsIgnoreCase(Relationship.NEUTRAL.toString())) {
            relationship = Relationship.NEUTRAL;
        } else {
            player.sendMessage(Messages.UNKNOWN_TYPE.getMessage(_relationship));
        }
        Player targetPlayer = player.getServer().getPlayer(playerName);
        if (targetPlayer != null) {
            PlayerStorage targetPlayerStorage = new PlayerStorage(targetPlayer.getUniqueId());
            PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
            if (playerStorage.getGuild() != null) {
                try {
                    playerStorage.getGuild().setRelationship(targetPlayerStorage.getGuild().getUuid(), relationship);
                    player.getServer().broadcastMessage(Messages.GUILD_ANNOUNCEMENT_DECLARE.getMessage(playerStorage.getGuild().getTag(),
                            targetPlayerStorage.getGuild().getTag(), relationship.toString()));
                    player.sendMessage(Messages.RELATIONSHIP_CHANGED.getMessage(targetPlayerStorage.getGuild().getTag(), relationship.toString()));
                } catch (IOException e) {
                    player.sendMessage(Messages.UNKNOWN_ERROR.getMessage());
                }
            } else {
                player.sendMessage(Messages.NOT_IN_LAND_OR_GUILD.getMessage());
            }
            playerStorage.getGuild();
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
        }
    }

    /**
     * Promote a player to a leader (kick, claim, unclaim)
     *
     * @param player       The player that is making someone a guild leader
     * @param targetPlayer The player that is being made a leader
     */

    public static void promote(Player player, String targetPlayer) {
        String username = targetPlayer;

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() == null) {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }

        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toMote = Bukkit.getPlayer(username);

            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                if (guildStorage.getMembers().contains(toMote.getUniqueId())) {
                    if (username.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Messages.GUILD_PROMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    if (guildStorage.getOwner() == toMote.getUniqueId()) {
                        player.sendMessage(Messages.GUILD_PROMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    if (guildStorage.getRole(toMote.getUniqueId()) == Role.LEADER) {
                        player.sendMessage(Messages.GUILD_PROMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    player.sendMessage(Messages.GUILD_PROMOTE_SUCCESS.getMessage(toMote.getName()));

                    try {
                        guildStorage.setRole(toMote.getUniqueId(), Role.LEADER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(Messages.NOT_IN_LAND_OR_GUILD.getMessage());
                    return;
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                return;
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }
    }

    /**
     * Demote someone if they are leader, to a member
     *
     * @param player       The player that is demoting someone
     * @param targetPlayer The player that is promoting someone
     */

    public static void demote(Player player, String targetPlayer) {
        String username = targetPlayer;

        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() == null) {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }

        if (Bukkit.getServer().getPlayer(username) != null) {
            Player toMote = Bukkit.getPlayer(username);

            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                if (guildStorage.getMembers().contains(toMote.getUniqueId())) {
                    if (username.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Messages.GUILD_DEMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    if (guildStorage.getOwner() == toMote.getUniqueId()) {
                        player.sendMessage(Messages.GUILD_DEMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    if (guildStorage.getRole(toMote.getUniqueId()) == Role.MEMBER) {
                        player.sendMessage(Messages.GUILD_DEMOTE_FAIL.getMessage(toMote.getName()));
                        return;
                    }

                    player.sendMessage(Messages.GUILD_DEMOTE_SUCCESS.getMessage(toMote.getName()));

                    try {
                        guildStorage.setRole(toMote.getUniqueId(), Role.MEMBER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage(Messages.NOT_IN_LAND_OR_GUILD.getMessage());
                    return;
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
                return;
            }
        } else {
            player.sendMessage(Messages.UNABLE_FIND_PLAYER.getMessage());
            return;
        }
    }

    public static void rename(Player player, String newName) {
        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
        if (playerStorage.getGuild() != null) {
            GuildStorage guildStorage = playerStorage.getGuild();

            if (guildStorage.getRole(player.getUniqueId()) == Role.OWNER
                    && guildStorage.getRole(player.getUniqueId()) != null) {

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(newName);
                if (m.find()) {
                    player.sendMessage(ChatColor.RED + "You cannot have any special characters in your guild name.");
                    return;
                }

                if (newName.length() > 6) {
                    player.sendMessage(ChatColor.RED + "Your guild name cannot be longer than 6 characters.");
                    return;
                }

                try {
                    player.sendMessage(Messages.RENAMED.getMessage(newName));
                    guildStorage.setName(newName);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.NO_PERMISSIONS.getMessage());
            }
        } else {
            player.sendMessage(Messages.NO_GUILD.getMessage());
            return;
        }
    }

}
