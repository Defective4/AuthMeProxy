package io.github.defective4.authmeproxy.velocity;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github.defective4.authmeproxy.common.annotations.DataFolder;
import io.github.defective4.authmeproxy.common.config.BungeeConfigProperties;
import io.github.defective4.authmeproxy.common.config.BungeeSettingsProvider;
import io.github.defective4.authmeproxy.velocity.commands.BungeeReloadCommand;
import io.github.defective4.authmeproxy.velocity.listeners.BungeeMessageListener;
import io.github.defective4.authmeproxy.velocity.listeners.BungeePlayerListener;
import io.github.defective4.authmeproxy.velocity.services.AuthPlayerManager;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;


@Plugin(name = "AuthMeProxy", id = "authmeproxy", authors = {"AuthMeTeam", "Defective4"}, version = "1.0")
public class AuthMeVelocity {

    private static ProxyServer staticServer;
    private static AuthMeVelocity INSTANCE;
    private final Logger logger;
    private final ProxyServer proxy;
    private final File dataFolder;
    // Instances
    private Injector injector;
    private SettingsManager settings;
    private AuthPlayerManager authPlayerManager;

    @Inject
    public AuthMeVelocity(Logger logger, ProxyServer proxy, @DataDirectory Path dataPath) {
        INSTANCE = this;
        this.logger = logger;
        this.proxy = proxy;
        this.dataFolder = dataPath.toFile();
        staticServer = proxy;
    }

    public static AuthMeVelocity getInstance() {
        return INSTANCE;
    }

    public static ProxyServer getProxyServer() {
        return staticServer;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent e) {
        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(SettingsManager.class);
        authPlayerManager = injector.getSingleton(AuthPlayerManager.class);

        // Print some config information
        getLogger().info("Current auth servers:");
        for (String authServer : settings.getProperty(BungeeConfigProperties.AUTH_SERVERS)) {
            getLogger().info("> " + authServer.toLowerCase());
        }

        // Add online players (plugin hotswap, just in case)
        for (Player player : getProxy().getAllPlayers()) {
            authPlayerManager.addAuthPlayer(player);
        }

        CommandManager commandManager = getProxy().getCommandManager();
        // Register commands
        commandManager.register(commandManager.metaBuilder("abreloadproxy").plugin(this).build(),
                                injector.getSingleton(BungeeReloadCommand.class));

        // Registering event listeners
        getProxy().getEventManager().register(this, injector.getSingleton(BungeeMessageListener.class));
        getProxy().getEventManager().register(this, injector.getSingleton(BungeePlayerListener.class));

        getProxy().getChannelRegistrar().register(MinecraftChannelIdentifier.from("authme:internal"));
        // Send metrics data
        //        new Metrics(this, 1880);
    }

    public File getDataFolder() {
        return dataFolder;
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("").create();
        injector.register(Logger.class, getLogger());
        injector.register(AuthMeVelocity.class, this);
        injector.register(ProxyServer.class, getProxy());
        injector.register(PluginManager.class, getProxy().getPluginManager());
        //        injector.register(TaskScheduler.class, getProxy().getScheduler());
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(SettingsManager.class, BungeeSettingsProvider.class);
    }

}
