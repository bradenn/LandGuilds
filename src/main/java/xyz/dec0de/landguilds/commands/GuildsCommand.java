package xyz.dec0de.landguilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Role;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildsCommand implements CommandExecutor {

    private HashMap<UUID, String> pendingGuildInvites = new HashMap<>();

    private String noGuildError = ChatColor.RED + "You are not apart of a guild. Please create or join one.";
    private String inGuildError = ChatColor.RED + "You are already apart of a guild, " +
            "you must leave or disband your current one.";
    private String noGuildPermissionsRole = ChatColor.RED +
            "You do not have enough permissions " +
            "in your guild to do this.";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                help(player);
            // CLAIM
            } else if (args[0].equalsIgnoreCase("claim")) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() != null) {
                    if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                        ChunkStorage chunkStorage = new ChunkStorage(
                                player.getWorld(),
                                player.getWorld().getChunkAt(player.getLocation()));

                        if (chunkStorage.isClaimed()) {
                            player.sendMessage(ChatColor.RED + "This chunk has already been claimed.");
                            return false;
                        }

                        GuildStorage guildStorage = playerStorage.getGuild();

                        if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER &&
                                guildStorage.getRole(player.getUniqueId()) != null) {
                            try {
                                player.sendMessage(ChatColor.GREEN + "Successfully claimed a chunk!");
                                guildStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                chunkStorage.claim(guildStorage.getUuid(), true);
                                return false;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(noGuildPermissionsRole);
                        }
                    }
                } else {
                    player.sendMessage(noGuildError);
                    return false;
                }

                // JOIN
            } else if (args[0].equalsIgnoreCase("unclaim")) {
                if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                    ChunkStorage chunkStorage = new ChunkStorage(
                            player.getWorld(),
                            player.getWorld().getChunkAt(player.getLocation()));

                    if (!chunkStorage.isClaimed()) {
                        player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                        return false;
                    }

                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        if (guildStorage.getMembers().contains(player.getUniqueId())) {
                            if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER
                                    && guildStorage.getRole(player.getUniqueId()) != null) {
                                player.sendMessage(ChatColor.GREEN +
                                        "You have unclaimed this chunk from your guild.");
                                try {
                                    guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                    chunkStorage.unclaim();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return false;

                            } else {
                                player.sendMessage(noGuildPermissionsRole);
                            }
                        } else {
                            player.sendMessage(
                                    ChatColor.RED +
                                            "You do not own this land.");
                        }
                        return false;
                    } else {
                        player.sendMessage(ChatColor.RED + "This land was not claimed by a guild.");
                        return false;
                    }
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (pendingGuildInvites.containsKey(player.getUniqueId())) {
                    PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                    if (playerStorage.getGuild() == null) {
                        UUID guildUuid = UUID.fromString(pendingGuildInvites.get(
                                player.getUniqueId()).split("\\_")[1]);
                        try {
                            playerStorage.setGuild(guildUuid);
                            GuildStorage guildStorage = new GuildStorage(guildUuid);
                            guildStorage.addMember(player.getUniqueId());

                            player.sendMessage(ChatColor.GREEN + "You have joined the guild "
                                    + guildStorage.getTag() + ChatColor.GREEN + ".");

                            pendingGuildInvites.remove(player.getUniqueId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(inGuildError);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have any pending invites.");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("disband")) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() != null) {
                    GuildStorage guildStorage = playerStorage.getGuild();

                    if (guildStorage.getRole(player.getUniqueId()) == Role.OWNER
                            && guildStorage.getRole(player.getUniqueId()) != null) {
                        try {
                            player.sendMessage(ChatColor.GREEN + "Successfully disbanded your guild!");
                            guildStorage.disbandGuild();
                            return false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(noGuildPermissionsRole);
                    }
                } else {
                    player.sendMessage(noGuildError);
                    return false;
                }

            } else if (args[0].equalsIgnoreCase("leave")) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() != null) {
                    if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                        GuildStorage guildStorage = playerStorage.getGuild();

                        if (guildStorage.getRole(player.getUniqueId()) != Role.OWNER
                                && guildStorage.getRole(player.getUniqueId()) != null) {
                            try {
                                player.sendMessage(ChatColor.GREEN + "You have left your current guild.");
                                playerStorage.removeGuild();
                                guildStorage.removeMember(player.getUniqueId());
                                return false;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You cannot leave your guild because you are the owner. You must disband or transfer it.");
                        }
                    }
                } else {
                    player.sendMessage(noGuildError);
                    return false;
                }

            }
        } else if (args.length == 2) {

            // CREATE
            if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() != null) {
                    player.sendMessage(inGuildError);
                    return false;
                }

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(name);

                if (m.find()) {
                    player.sendMessage(ChatColor.RED + "You cannot have any special characters in your guild name.");
                    return false;
                }

                if (name.length() > 6) {
                    player.sendMessage(ChatColor.RED + "Your guild name cannot be longer than 6 characters.");
                    return false;
                }

                try {
                    GuildStorage guildStorage = new GuildStorage(name, player.getUniqueId());
                    playerStorage.setGuild(guildStorage.getUuid());

                    player.sendMessage(ChatColor.GREEN + "You have created the guild named " + name + ".");
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // INVITE
            } else if (args[0].equalsIgnoreCase("invite")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() == null) {
                    player.sendMessage(noGuildError);
                    return false;
                }
                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toInvite = Bukkit.getPlayer(username);
                    PlayerStorage invitePlayerStorage = new PlayerStorage(toInvite.getUniqueId());

                    if (pendingGuildInvites.containsKey(toInvite.getUniqueId())) {
                        player.sendMessage(ChatColor.RED +
                                "This player already has a pending invite, try again in a little bit.");
                        return false;
                    }


                    GuildStorage guildStorage = playerStorage.getGuild();

                    if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER
                            && guildStorage.getRole(player.getUniqueId()) != null) {
                        String guildToken = guildStorage.getName() + "_" + guildStorage.getUuid().toString();
                        pendingGuildInvites.put(toInvite.getUniqueId(), guildToken);

                        player.sendMessage(ChatColor.GREEN + "You have invited " + toInvite.getName() + " to join your guild.");

                        toInvite.sendMessage(ChatColor.GREEN + "You have been invited to join " +
                                guildStorage.getTag() + ChatColor.GREEN + ". Expires in 30 seconds. Type /guilds join");

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                            public void run() {
                                if (pendingGuildInvites.containsKey(toInvite.getUniqueId()) &&
                                        pendingGuildInvites.get(toInvite.getUniqueId()) == guildToken) {
                                    toInvite.sendMessage(ChatColor.RED + "The invite has expired!");
                                    pendingGuildInvites.remove(toInvite.getUniqueId());
                                }
                            }
                        }, 30 * 20L); // 20 ticks = 1 second. So 100 ticks = 5 seconds.
                    } else {
                        player.sendMessage(noGuildPermissionsRole);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("kick")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() == null) {
                    player.sendMessage(noGuildError);
                    return false;
                }

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toKick = Bukkit.getPlayer(username);
                    PlayerStorage toKickPlayerStorage = new PlayerStorage(toKick.getUniqueId());

                    GuildStorage guildStorage = playerStorage.getGuild();

                    if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                        if (guildStorage.getMembers().contains(toKick.getUniqueId())) {
                            if (username.equalsIgnoreCase(player.getName())) {
                                player.sendMessage(ChatColor.RED + "You cannot kick yourself.");
                                return false;
                            }

                            if (guildStorage.getOwner() == toKick.getUniqueId()) {
                                player.sendMessage(ChatColor.RED +
                                        "You cannot kick the owner! They must disband or transfer the guild.");
                                return false;
                            }

                            player.sendMessage(ChatColor.GREEN + "Successfully removed " + toKick.getName() + " from the guild.");

                            try {
                                toKickPlayerStorage.removeGuild();
                                guildStorage.removeMember(toKick.getUniqueId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "This player is not in the guild.");
                            return false;
                        }
                    } else {
                        player.sendMessage(noGuildPermissionsRole);
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }

                //PROMOTE
            } else if (args[0].equalsIgnoreCase("promote")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() == null) {
                    player.sendMessage(noGuildError);
                    return false;
                }

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toMote = Bukkit.getPlayer(username);

                    GuildStorage guildStorage = playerStorage.getGuild();

                    if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                        if (guildStorage.getMembers().contains(toMote.getUniqueId())) {
                            if (username.equalsIgnoreCase(player.getName())) {
                                player.sendMessage(ChatColor.RED + "You cannot promote yourself.");
                                return false;
                            }

                            if (guildStorage.getOwner() == toMote.getUniqueId()) {
                                player.sendMessage(ChatColor.RED +
                                        "You promote kick the owner! They must disband or transfer the guild.");
                                return false;
                            }

                            if (guildStorage.getRole(toMote.getUniqueId()) == Role.LEADER) {
                                player.sendMessage(ChatColor.RED + "This player is already a leader, and cannot be promoted.");
                                return false;
                            }

                            player.sendMessage(ChatColor.GREEN + "Successfully promoted " + toMote.getName() + " in the guild.");

                            try {
                                guildStorage.setRole(toMote.getUniqueId(), Role.LEADER);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "This player is not in the guild.");
                            return false;
                        }
                    } else {
                        player.sendMessage(noGuildPermissionsRole);
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("demote")) {
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (playerStorage.getGuild() == null) {
                    player.sendMessage(noGuildError);
                    return false;
                }

                if (Bukkit.getServer().getPlayer(username) != null) {
                    Player toMote = Bukkit.getPlayer(username);

                    GuildStorage guildStorage = playerStorage.getGuild();

                    if (guildStorage.getRole(player.getUniqueId()) != Role.MEMBER && guildStorage.getRole(player.getUniqueId()) != null) {
                        if (guildStorage.getMembers().contains(toMote.getUniqueId())) {
                            if (username.equalsIgnoreCase(player.getName())) {
                                player.sendMessage(ChatColor.RED + "You cannot demote yourself.");
                                return false;
                            }

                            if (guildStorage.getOwner() == toMote.getUniqueId()) {
                                player.sendMessage(ChatColor.RED +
                                        "You cannot demote the owner! They must disband or transfer the guild.");
                                return false;
                            }

                            if (guildStorage.getRole(toMote.getUniqueId()) == Role.MEMBER) {
                                player.sendMessage(ChatColor.RED + "This player is only a member and cannot be demoted.");
                                return false;
                            }

                            player.sendMessage(ChatColor.GREEN + "Successfully demoted " + toMote.getName() + " in the guild.");

                            try {
                                guildStorage.setRole(toMote.getUniqueId(), Role.MEMBER);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "This player is not in the guild.");
                            return false;
                        }
                    } else {
                        player.sendMessage(noGuildPermissionsRole);
                        return false;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            }
        } else {
            help(player);
        }
        return false;
    }

    public void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Guilds Help -*-*-*-*-*-");
        player.sendMessage(ChatColor.GRAY + "/g help " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Displays help");
        player.sendMessage(ChatColor.GRAY + "/g create [name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Create a guild");
        player.sendMessage(ChatColor.GRAY + "/g disband [name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Disband your guild");
        player.sendMessage(ChatColor.GRAY + "/g claim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Claim a chunk for your guild");
        player.sendMessage(ChatColor.GRAY + "/g join " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Join a guild if you have an invite");
        player.sendMessage(ChatColor.GRAY + "/g leave " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Leave your current guild");
        player.sendMessage(ChatColor.GRAY + "/g unclaim " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Unclaim a chunk from your guild");
        player.sendMessage(ChatColor.GRAY + "/g invite [player name]" + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Invite a player to your guild");
        player.sendMessage(ChatColor.GRAY + "/g kick [player name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Kick a player from your guild");
        player.sendMessage(ChatColor.GRAY + "/g promote [player name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Promote a player in the guild to a leader");
        player.sendMessage(ChatColor.GRAY + "/g demote [player name] " + ChatColor.DARK_GRAY + "-" + ChatColor.WHITE + " Demote a player from leader to member");
        player.sendMessage(ChatColor.GREEN + "-*-*-*-*-*- Guilds Help -*-*-*-*-*-");
    }
}
