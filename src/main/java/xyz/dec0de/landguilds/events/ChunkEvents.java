package xyz.dec0de.landguilds.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
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
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void animalAttack(EntityDamageByEntityEvent e) {
        if (Main.allowedWorlds().contains(e.getDamager().getLocation().getWorld().getName())) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Animals) {
                Player damager = (Player) e.getDamager();
                Entity entity = e.getEntity();

                ChunkStorage chunk = new ChunkStorage(entity.getWorld(), entity.getLocation().getChunk());

                if (chunk.isClaimed()) {
                    if (chunk.isGuild()) {
                        GuildStorage guild = new GuildStorage(chunk.getOwner());
                        if (!guild.getMembers().contains(damager.getUniqueId())) {
                            damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                            e.setCancelled(true);
                        }
                    } else {
                        if (!chunk.getMembers().contains(damager)) {
                            damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerPVP(EntityDamageByEntityEvent e) {
        if (Main.allowedWorlds().contains(e.getDamager().getLocation().getWorld().getName())) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player player = (Player) e.getEntity();

                PlayerStorage damagerStorage = new PlayerStorage(damager.getUniqueId());
                if (damagerStorage.getGuild() != null) {
                    GuildStorage guildStorage = new GuildStorage(damagerStorage.getGuild().getUuid());

                    if (guildStorage.getMembers().contains(player.getUniqueId())) {
                        if (guildStorage.getChunks().contains(player.getLocation().getChunk())) {
                            e.setCancelled(true);
                            damager.sendMessage(Messages.NO_GUILD_PVP.getMessage());
                        }
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
                        player.sendMessage(Messages.NO_BREAK.getMessage());
                        e.setCancelled(true);
                    }
                } else {
                    if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(Messages.NO_BREAK.getMessage());
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
                        player.sendMessage(Messages.NO_BUILD.getMessage());
                        e.setCancelled(true);
                    }
                } else {
                    if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                        player.sendMessage(Messages.NO_BUILD.getMessage());
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
                        if (!blockHasAllowSign(player, e.getClickedBlock())) {
                            if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                                player.sendMessage(Messages.NO_INTERACT.getMessage());
                                e.setCancelled(true);
                            }
                        }
                    } else {
                        if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                            if (!blockHasAllowSign(player, e.getClickedBlock())) {
                                player.sendMessage(Messages.NO_INTERACT.getMessage());
                                e.setCancelled(true);
                            }
                        }
                    }
                }
        }
    }

    private boolean blockHasAllowSign(Player player, Block block) {
        List<Sign> sign = new ArrayList<>();
        for (BlockFace blockFace : BlockFace.values()) {
            Block br = block.getRelative(blockFace, 1);
            if (br.getBlockData().getMaterial().toString().contains("SIGN")) {
                Sign sign1 = (Sign) br.getState();
                sign.add(sign1);
            }
        }

        for (Sign signCheck : sign) {
            if (signCheck.getLine(0).equalsIgnoreCase("[LandGuilds]")) {
                if (signCheck.getLine(1).equalsIgnoreCase("*Everyone*")) {
                    return true;
                }
            }
        }

        return false;
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
