package xyz.dec0de.landguilds.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.dec0de.landguilds.Main;
import xyz.dec0de.landguilds.enums.Messages;
import xyz.dec0de.landguilds.enums.Tags;
import xyz.dec0de.landguilds.handlers.AdminHandler;
import xyz.dec0de.landguilds.storage.ChunkStorage;
import xyz.dec0de.landguilds.storage.GuildStorage;
import xyz.dec0de.landguilds.storage.PlayerStorage;

import java.util.*;

public class ChunkEvents implements Listener {

    private final HashMap<UUID, String> playersInChunk = new HashMap<>();

    public boolean playerCanInteract(Player player, Location location) {
        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
            ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(location));
            if (AdminHandler.isOverride(player.getUniqueId())) return true;
            if (chunkStorage.isClaimed()) {
                if (chunkStorage.isGuild()) {
                    GuildStorage guild = new GuildStorage(chunkStorage.getOwner());
                    return guild.getMembers().contains(player.getUniqueId()) || guild.getTrusted().contains(player.getUniqueId());
                } else {
                    return chunkStorage.getMembers().contains(player.getUniqueId());
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
            Player player = e.getPlayer();
            if (Main.allowedWorlds().contains(player.getWorld().getName())) {
                ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
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
    public void onFireIgnite(BlockIgniteEvent e) {
        if (Main.allowedWorlds().contains(Objects.requireNonNull(e.getBlock().getWorld()).getName())) {
            ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getBlock().getWorld(), e.getBlock().getChunk());
            if (e.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
                if (chunkStorage.isClaimed()) {
                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        if (!guildStorage.getTag(Tags.FIRESPREAD)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void animalAttack(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();

        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getDamager().getLocation().getWorld()).getName())) {
            return;
        }

        if (e.getDamager() instanceof Projectile && e.getEntity() instanceof ItemFrame) {
            ChunkStorage chunk = ChunkStorage.getChunk(entity.getWorld(), entity.getLocation().getChunk());

            if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
                return;
            }
            Player damager = (Player) ((Projectile) e
                    .getDamager()).getShooter();

            if (AdminHandler.isOverride(damager.getUniqueId())) {
                return;
            }
            if (!chunk.isClaimed()) {
                return;
            }
            if (chunk.isGuild()) {
                GuildStorage guild = new GuildStorage(chunk.getOwner());
                if (!guild.getMembers().contains(damager.getUniqueId())) {
                    damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                    e.getDamager().remove();
                    e.setCancelled(true);
                    return;
                }
                return;
            }
            if (!chunk.getMembers().contains(damager.getUniqueId())) {
                damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                e.getDamager().remove();
                e.setCancelled(true);
                return;
            }
        }
        if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Animals || e.getEntity() instanceof ItemFrame)) {
            Player damager = (Player) e.getDamager();
            ChunkStorage chunk = ChunkStorage.getChunk(entity.getWorld(), entity.getLocation().getChunk());
            if (AdminHandler.isOverride(damager.getUniqueId())) {
                return;
            }
            if (!chunk.isClaimed()) {
                return;
            }
            if (chunk.isGuild()) {
                GuildStorage guild = new GuildStorage(chunk.getOwner());
                if (!guild.getMembers().contains(damager.getUniqueId())) {
                    damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                    e.setCancelled(true);
                    return;
                }
                return;
            }
            if (!chunk.getMembers().contains(damager.getUniqueId())) {
                damager.sendMessage(Messages.NO_KILL_ANIMAL.getMessage());
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void itemframeDestroy(HangingBreakEvent e) {
        ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk());

        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getEntity().getLocation().getWorld()).getName())) {
            return;
        }
        if (e.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION) {
            return;
        }

        if (!chunkStorage.isClaimed()) {
            return;
        }

        if (!chunkStorage.isGuild()) {
            return;
        }
        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
        if (!guildStorage.getTag(Tags.BOOM)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerPVP(EntityDamageByEntityEvent e) {
        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getDamager().getLocation().getWorld()).getName())) {
            return;
        }
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();

            ChunkStorage chunkStorage = ChunkStorage.getChunk(damager.getLocation().getWorld(), damager.getLocation().getChunk());
            if (!chunkStorage.isClaimed()) {
                return;
            }

            if (!chunkStorage.isGuild()) {
                return;
            }
            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
            if (!guildStorage.getTag(Tags.PVP)) {
                e.setCancelled(true);
                damager.sendMessage(Messages.NO_GUILD_PVP.getMessage());
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName())) {
            return;
        }
        Player player = e.getPlayer();
        if (!playerCanInteract(player, e.getBlock().getLocation())) {
            player.sendMessage(Messages.NO_BREAK.getMessage());
            e.setCancelled(true);
        }
    }
//        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
//            if (!AdminHandler.isOverride(player.getUniqueId())) {
//                ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(e.getBlock().getLocation()));
//                if (chunkStorage.isClaimed()) {
//                    new PlayerStorage(player.getUniqueId());
//                    if (chunkStorage.isGuild()) {
//                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
//                        if (!guildStorage.getMembers().contains(player.getUniqueId())) {
//                            player.sendMessage(Messages.NO_BREAK.getMessage());
//                            e.setCancelled(true);
//                        }
//                    } else {
//                        if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
//                            player.sendMessage(Messages.NO_BREAK.getMessage());
//                            e.setCancelled(true);
//                        }
//                    }
//                }
//            }
//        }

    @EventHandler
    public void placeBlock(BlockPlaceEvent e) {
        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName())) {
            return;
        }
        Player player = e.getPlayer();
        if (!playerCanInteract(player, e.getBlock().getLocation())) {
            player.sendMessage(Messages.NO_BUILD.getMessage());
            e.setCancelled(true);
        }
    }
//        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
//            if (!AdminHandler.isOverride(player.getUniqueId())) {
//                ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(e.getBlock().getLocation()));
//                if (chunkStorage.isClaimed()) {
//                    new PlayerStorage(player.getUniqueId());
//                    if (chunkStorage.isGuild()) {
//                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
//                        if (!guildStorage.getMembers().contains(player.getUniqueId()) && !guildStorage.getTrusted().contains(player.getUniqueId())) {
//                            player.sendMessage(Messages.NO_BUILD.getMessage());
//                            e.setCancelled(true);
//                        }
//                    } else {
//                        if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
//                            player.sendMessage(Messages.NO_BUILD.getMessage());
//                            e.setCancelled(true);
//                        }
//                    }
//                }
//            }
//        }


    @EventHandler
    public void rotateItemFrame(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();

        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName())) {
            return;
        }
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (!AdminHandler.isOverride(player.getUniqueId())) {
                ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(player.getLocation()));
                if (chunkStorage.isClaimed()) {
                    new PlayerStorage(player.getUniqueId());
                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                            player.sendMessage(Messages.NO_INTERACT.getMessage());
                            e.setCancelled(true);
                        }
                    } else {
                        if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
                            player.sendMessage(Messages.NO_INTERACT.getMessage());
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!Main.allowedWorlds().contains(Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName())) {
            return;
        }

        if (e.getClickedBlock() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().toString().contains("SIGN"))
            return;

        if (blockHasEveryoneSign(player, e.getClickedBlock()))
            return;

        if (!playerCanInteract(player, e.getClickedBlock().getLocation())) {
            player.sendMessage(Messages.NO_INTERACT.getMessage());
            e.setCancelled(true);
        }

//        if (Main.allowedWorlds().contains(player.getWorld().getName())) {
//            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType().toString().contains("SIGN")) {
//                e.setCancelled(false);
//            } else {
//                if (!AdminHandler.isOverride(player.getUniqueId())) {
//                    ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getWorld().getChunkAt(e.getClickedBlock().getLocation()));
//                    if (chunkStorage.isClaimed()) {
//                        if (chunkStorage.isGuild()) {
//                            GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
//                            if (!blockHasAllowSign(player, e.getClickedBlock())) {
//                                if (!guildStorage.getMembers().contains(player.getUniqueId())) {
//                                    player.sendMessage(Messages.NO_INTERACT.getMessage());
//                                    e.setCancelled(true);
//                                }
//                            }
//                        } else {
//                            if (!chunkStorage.getMembers().contains(player.getUniqueId())) {
//                                if (!blockHasAllowSign(player, e.getClickedBlock())) {
//                                    player.sendMessage(Messages.NO_INTERACT.getMessage());
//                                    e.setCancelled(true);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    @EventHandler
    public void onEntityInteract(EntityBreedEvent e) {
        Player player = (Player) e.getBreeder();
        if (player != null && Main.allowedWorlds().contains(player.getWorld().getName())) {
            if (!AdminHandler.isOverride(player.getUniqueId())) {
                ChunkStorage chunkStorage = ChunkStorage.getChunk(player.getWorld(), player.getLocation().getChunk());
                if (chunkStorage.isClaimed()) {
                    if (chunkStorage.isGuild()) {
                        GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                        if (!guildStorage.getMembers().contains(player.getUniqueId())) {
                            e.getEntity().remove();
                            player.sendMessage(Messages.NO_BREED.getMessage());
                        }
                    }
                }
            }
        }
    }

    private boolean blockHasEveryoneSign(Player player, Block block) {
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
    public void onBoom(BlockExplodeEvent e) {
        if (Main.allowedWorlds().contains(Objects.requireNonNull(e.getBlock().getLocation().getWorld().getName()))) {
            for (Block block : e.blockList()) {
                ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getBlock().getLocation().getWorld(), block.getChunk());
                if (!chunkStorage.isClaimed()) {
                    return;
                }

                if (!chunkStorage.isGuild()) {
                    return;
                }

                GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                if (!guildStorage.getTag(Tags.BOOM)) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBoom(EntityExplodeEvent e) {
        if (Main.allowedWorlds().contains(Objects.requireNonNull(e.getLocation().getWorld().getName()))) {
            for (Block block : e.blockList()) {
                ChunkStorage chunkStorage = ChunkStorage.getChunk(e.getLocation().getWorld(), block.getChunk());
                if (!chunkStorage.isClaimed()) {
                    return;
                }

                if (!chunkStorage.isGuild()) {
                    return;
                }

                GuildStorage guildStorage = new GuildStorage(chunkStorage.getOwner());
                if (!guildStorage.getTag(Tags.BOOM)) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

}
