package io.github.defective4.authmeproxy.bungee;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import io.github.defective4.authmeproxy.bungee.commands.BungeeReloadCommand;
import io.github.defective4.authmeproxy.bungee.listeners.BungeeMessageListener;
import io.github.defective4.authmeproxy.bungee.listeners.BungeePlayerListener;
import io.github.defective4.authmeproxy.bungee.services.BungeeAuthPlayerManager;
import io.github.defective4.authmeproxy.common.annotations.DataFolder;
import io.github.defective4.authmeproxy.common.config.ProxyConfigProperties;
import io.github.defective4.authmeproxy.common.config.ProxySettingsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.bstats.bungeecord.Metrics;

import java.util.logging.Logger;

public class AuthMeBungee extends Plugin {

    private static AuthMeBungee INSTANCE;
    // Instances
    private Injector injector;
    private SettingsManager settings;
    private BungeeAuthPlayerManager authPlayerManager;

    public AuthMeBungee() {
    }

    public AuthMeBungee getInstance() {
        return INSTANCE;
    }

    public BungeeAuthPlayerManager getAuthPlayerManager() {
        return authPlayerManager;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(SettingsManager.class);
        authPlayerManager = injector.getSingleton(BungeeAuthPlayerManager.class);

        // Print some config information
        getLogger().info("Current auth servers:");
        for (String authServer : settings.getProperty(ProxyConfigProperties.AUTH_SERVERS)) {
            getLogger().info("> " + authServer.toLowerCase());
        }

        // Add online players (plugin hotswap, just in case)
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            authPlayerManager.addAuthPlayer(player);
        }

        // Register commands
        getProxy().getPluginManager().registerCommand(this, injector.getSingleton(BungeeReloadCommand.class));

        // Registering event listeners
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeeMessageListener.class));
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeePlayerListener.class));

        // Send metrics data
        new Metrics(this, 21778);
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("").create();
        injector.register(Logger.class, getLogger());
        injector.register(AuthMeBungee.class, this);
        injector.register(ProxyServer.class, getProxy());
        injector.register(PluginManager.class, getProxy().getPluginManager());
        injector.register(TaskScheduler.class, getProxy().getScheduler());
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(SettingsManager.class, ProxySettingsProvider.class);
    }

}
