package xyz.dec0de.landguilds;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dec0de.landguilds.commands.GuildsCommand;
import xyz.dec0de.landguilds.commands.LandsCommand;
import xyz.dec0de.landguilds.events.ChunkEvents;
import xyz.dec0de.landguilds.events.PlayerEvents;

import java.util.List;

public class Main extends JavaPlugin {

    public static FileConfiguration config;
    private static Main plugin;
    private static Chat chat;

    public static Main getPlugin() {
        return plugin;
    }

    public static List<String> allowedWorlds() {
        return config.getStringList("worlds");
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onDisable() {

    }

    /**
     * TODO
     * Guilds:
     * - Disbanding Guilds (only for owners)
     * - Leaving guilds
     * - Transfering ownership
     * - Roles
     * - Levels
     * - Vault
     * - GUIs
     * Lands:
     * - Homes
     * - Permissions for claims and such
     */

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();

        plugin.saveDefaultConfig();

        plugin.getCommand("lands").setExecutor(new LandsCommand());
        plugin.getCommand("guilds").setExecutor(new GuildsCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChunkEvents(), plugin);

        setupChat();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
}