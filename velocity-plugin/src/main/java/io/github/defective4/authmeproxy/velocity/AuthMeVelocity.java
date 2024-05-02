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
import io.github.defective4.authmeproxy.common.config.ProxyConfigProperties;
import io.github.defective4.authmeproxy.common.config.ProxySettingsProvider;
import io.github.defective4.authmeproxy.velocity.commands.VelocityReloadCommand;
import io.github.defective4.authmeproxy.velocity.listeners.VelocityMessageListener;
import io.github.defective4.authmeproxy.velocity.listeners.VelocityPlayerListener;
import io.github.defective4.authmeproxy.velocity.services.VelocityAuthPlayerManager;
import org.bstats.velocity.Metrics;
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
    private final Metrics.Factory metricsFactory;
    // Instances
    private Injector injector;
    private SettingsManager settings;
    private VelocityAuthPlayerManager authPlayerManager;

    @Inject
    public AuthMeVelocity(
        Logger logger, ProxyServer proxy, @DataDirectory Path dataPath, Metrics.Factory metricsFactory
    ) {
        this.metricsFactory = metricsFactory;
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

    public VelocityAuthPlayerManager getAuthPlayerManager() {
        return authPlayerManager;
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
        authPlayerManager = injector.getSingleton(VelocityAuthPlayerManager.class);

        // Print some config information
        getLogger().info("Current auth servers:");
        for (String authServer : settings.getProperty(ProxyConfigProperties.AUTH_SERVERS)) {
            getLogger().info("> " + authServer.toLowerCase());
        }

        // Add online players (plugin hotswap, just in case)
        for (Player player : getProxy().getAllPlayers()) {
            authPlayerManager.addAuthPlayer(player);
        }

        CommandManager commandManager = getProxy().getCommandManager();
        // Register commands
        commandManager.register(commandManager.metaBuilder("abreloadproxy").plugin(this).build(),
                                injector.getSingleton(VelocityReloadCommand.class));

        // Registering event listeners
        getProxy().getEventManager().register(this, injector.getSingleton(VelocityMessageListener.class));
        getProxy().getEventManager().register(this, injector.getSingleton(VelocityPlayerListener.class));

        getProxy().getChannelRegistrar().register(MinecraftChannelIdentifier.from("authme:internal"));
        // Send metrics data
        metricsFactory.make(this, 21777);
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
        injector.registerProvider(SettingsManager.class, ProxySettingsProvider.class);
    }

}
