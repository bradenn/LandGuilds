package xyz.dec0de.landguilds.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.util.HashMap;
import java.util.UUID;

public class ChunkEvents implements Listener {

    private HashMap<UUID, String> playersInChunk = new HashMap<>();

    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {

        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            Player player = e.getPlayer();
            if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                if (chunkStorage.isClaimed()) {
                    String landOwner;

                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        landOwner = guildStorage.getTag();
                    } else {
                        PlayerStorage playerStorage = new PlayerStorage(chunkStorage.getOwner());
                        landOwner = playerStorage.getUsername();
                    }

                    if (playersInChunk.containsKey(player.getUniqueId())) {
                        if (!playersInChunk.get(player.getUniqueId()).equalsIgnoreCase(landOwner)) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                    ChatColor.GREEN + "Entering " + landOwner + ChatColor.GREEN + "'s Land..."));
                            playersInChunk.put(player.getUniqueId(), landOwner);
                        }
                    } else {
                        playersInChunk.put(player.getUniqueId(), landOwner);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                ChatColor.GREEN + "Entering " + landOwner + ChatColor.GREEN + "'s Land..."));
                    }
                } else {
                    if (playersInChunk.containsKey(player.getUniqueId())) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                                ChatColor.RED + "Leaving " + playersInChunk.get(player.getUniqueId()) + ChatColor.RED + "'s Land..."));
                        playersInChunk.remove(player.getUniqueId());
                    }
                }
            }
        });
    }

    @EventHandler
    public void playerPVP(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player player = (Player) e.getEntity();

            PlayerStorage damagerStorage = new PlayerStorage(damager.getUniqueId());
            if (damagerStorage.getGuild() != null) {
                GuildStorage guildStorage = new GuildStorage(damagerStorage.getGuild().getUuid());

                if (guildStorage.getMembers().contains(player.getUniqueId())) {
                    if (guildStorage.getChunks().contains(player.getLocation().getChunk())) {
                        e.setCancelled(true);
                        damager.sendMessage(ChatColor.RED + "You cannot attack a player that is in your guild, while you're in guild land.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(e.getBlock().getLocation()));
            if (chunkStorage.isClaimed()) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (chunkStorage.isGuild()) {
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot break here.");
                        e.setCancelled(true);
                    }
                } else {
                    if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot break here.");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
            ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(e.getBlock().getLocation()));
            if (chunkStorage.isClaimed()) {
                PlayerStorage playerStorage = new PlayerStorage(player.getUniqueId());
                if (chunkStorage.isGuild()) {
                    GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                    if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot place here.");
                        e.setCancelled(true);
                    }
                } else {
                    if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot place here.");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getClickedBlock() == null) return;

        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                ChunkStorage chunkStorage = new ChunkStorage(player.getWorld(), player.getWorld().getChunkAt(e.getClickedBlock().getLocation()));
                if (chunkStorage.isClaimed()) {
                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot interact here.");
                            e.setCancelled(true);
                        }
                    } else {
                        if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "This chunk is claimed. You cannot interact here.");
                            e.setCancelled(true);
                        }
                    }
                }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        Location location = e.getLocation();

        if (Main.allowedWorlds().contains(location.getWorld().getName())) {
            Chunk chunk = location.getChunk();
            World world = location.getWorld();

            ChunkStorage chunkStorage = new ChunkStorage(world, chunk);
            if (chunkStorage.isClaimed()) {
                e.setCancelled(true);
            }
        }
    }
}
