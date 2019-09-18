package xyz.dec0de.landguilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.dec0de.landguilds.Main;
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
    private String inGuildError = ChatColor.RED + "You are already apart of a guild, you must leave or disband your current one.";
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

                // CLAIM
                if (args[0].equalsIgnoreCase("claim")) {
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

                        if(guildStorage.getOwner() == player.getUniqueId()) {

                            try {
                                player.sendMessage(ChatColor.GREEN + "Successfully claimed a chunk!");
                                guildStorage.addChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                chunkStorage.claim(guildStorage.getUuid(), true);
                                return false;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            player.sendMessage(noGuildPermissionsRole);
                        }
                    }
                    }else{
                        player.sendMessage(noGuildError);
                        return false;
                    }

                    // JOIN
                }else if (args[0].equalsIgnoreCase("unclaim")) {
                    if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                        ChunkStorage chunkStorage = new ChunkStorage(
                                player.getWorld(),
                                player.getWorld().getChunkAt(player.getLocation()));

                        if(!chunkStorage.isClaimed()){
                            player.sendMessage(ChatColor.RED + "This chunk is not claimed.");
                            return false;
                        }

                        if(chunkStorage.isGuild()){
                            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                            if(guildStorage.getOwner() == player.getUniqueId()) {
                                player.sendMessage(ChatColor.GREEN +
                                        "You have unclaimed this chunk from your guild.");
                                try {
                                    guildStorage.removeChunk(chunkStorage.getWorld(), chunkStorage.getChunk());
                                    chunkStorage.unclaim();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return false;

                            }else{
                                player.sendMessage(noGuildPermissionsRole);
                            }
                            return false;
                        }else{
                            player.sendMessage(ChatColor.RED + "This land was not claimed by a guild.");
                            return false;
                        }
                    }
                }else if(args[0].equalsIgnoreCase("join")){
                    if(pendingGuildInvites.containsKey(player.getUniqueId())){
                        PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                        if(playerStorage.getGuild() == null){
                            UUID guildUuid = UUID.fromString(pendingGuildInvites.get(
                                    player.getUniqueId()).split("\\_")[1]);
                            try {
                                playerStorage.setGuild(guildUuid);
                                GuildStorage guildStorage = new GuildStorage(guildUuid);
                                guildStorage.addMember(player.getUniqueId());

                                player.sendMessage(ChatColor.GREEN + "You have joined the guild "
                                        + guildStorage.getTag() + ChatColor.GREEN + ".");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            player.sendMessage(inGuildError);
                        }
                    }
                }
        }else if(args.length == 2){

            // CREATE
            if(args[0].equalsIgnoreCase("create")){
                String name = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if(playerStorage.getGuild() != null){
                    player.sendMessage(inGuildError);
                    return false;
                }

                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(name);

                if(m.find()){
                    player.sendMessage(ChatColor.RED + "You cannot have any special characters in your guild name.");
                    return false;
                }

                if(name.length() > 6){
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
            }else if(args[0].equalsIgnoreCase("invite")){
                String username = args[1];

                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if(playerStorage.getGuild() == null){
                    player.sendMessage(noGuildError);
                    return false;
                }
                if(Bukkit.getServer().getPlayer(username).isOnline()){
                    Player toInvite = Bukkit.getPlayer(username);
                    PlayerStorage invitePlayerStorage = new PlayerStorage(toInvite.getUniqueId());

                    if(pendingGuildInvites.containsKey(toInvite.getUniqueId())){
                        player.sendMessage(ChatColor.RED + "This player already has a pending invite, try again in a little bit.");
                        return false;
                    }


                    GuildStorage guildStorage = playerStorage.getGuild();

                    if(guildStorage.getOwner() == player.getUniqueId()) {
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
                    }else{
                        player.sendMessage(noGuildPermissionsRole);
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                    return false;
                }
            }// KICK
        }else if(args[0].equalsIgnoreCase("kick")){
            String username = args[1];

            PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
            if(playerStorage.getGuild() == null){
                player.sendMessage(noGuildError);
                return false;
            }

            if(Bukkit.getServer().getPlayer(username).isOnline()){
                Player toKick = Bukkit.getPlayer(username);
                PlayerStorage toKickPlayerStorage = new PlayerStorage(toKick.getUniqueId());


                GuildStorage guildStorage = playerStorage.getGuild();

                if(guildStorage.getOwner() == player.getUniqueId()) {
                    if(guildStorage.getMembers().contains(toKick.getUniqueId())) {
                        player.sendMessage(ChatColor.GREEN + "Successfully removed " + toKick.getName() + " from the guild.");

                        try {
                            toKickPlayerStorage.setGuild(null);
                            guildStorage.removeMember(toKick.getUniqueId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    player.sendMessage(noGuildPermissionsRole);
                }
            }else{
                player.sendMessage(ChatColor.RED + "Unable to find player. Make sure they are online.");
                return false;
            }
        }
        return false;
    }
}
