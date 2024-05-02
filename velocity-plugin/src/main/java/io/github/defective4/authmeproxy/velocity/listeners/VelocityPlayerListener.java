package io.github.defective4.authmeproxy.velocity.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.defective4.authmeproxy.common.config.ProxyConfigProperties;
import io.github.defective4.authmeproxy.common.config.SettingsDependent;
import io.github.defective4.authmeproxy.velocity.data.AuthPlayer;
import io.github.defective4.authmeproxy.velocity.services.AuthPlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VelocityPlayerListener implements SettingsDependent {

    // Services
    private final AuthPlayerManager authPlayerManager;

    // Settings
    private boolean isAutoLoginEnabled;
    private boolean isServerSwitchRequiresAuth;
    private String requiresAuthKickMessage;
    private List<String> authServers;
    private boolean allServersAreAuthServers;
    private boolean isCommandsRequireAuth;
    private List<String> commandWhitelist;
    private boolean chatRequiresAuth;

    @Inject
    public VelocityPlayerListener(final SettingsManager settings, final AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(final SettingsManager settings) {
        isAutoLoginEnabled = settings.getProperty(ProxyConfigProperties.AUTOLOGIN);
        isServerSwitchRequiresAuth = settings.getProperty(ProxyConfigProperties.SERVER_SWITCH_REQUIRES_AUTH);
        requiresAuthKickMessage = settings.getProperty(ProxyConfigProperties.SERVER_SWITCH_KICK_MESSAGE);
        authServers = new ArrayList<>();
        for (final String server : settings.getProperty(ProxyConfigProperties.AUTH_SERVERS)) {
            authServers.add(server.toLowerCase());
        }
        allServersAreAuthServers = settings.getProperty(ProxyConfigProperties.ALL_SERVERS_ARE_AUTH_SERVERS);
        isCommandsRequireAuth = settings.getProperty(ProxyConfigProperties.COMMANDS_REQUIRE_AUTH);
        commandWhitelist = new ArrayList<>();
        for (final String command : settings.getProperty(ProxyConfigProperties.COMMANDS_WHITELIST)) {
            commandWhitelist.add(command.toLowerCase());
        }
        chatRequiresAuth = settings.getProperty(ProxyConfigProperties.CHAT_REQUIRES_AUTH);
    }

    @Subscribe
    public void onPlayerJoin(final PostLoginEvent event) {
        // Register player in our list
        authPlayerManager.addAuthPlayer(event.getPlayer());
    }

    @Subscribe
    public void onPlayerDisconnect(final DisconnectEvent event) {
        // Remove player from out list
        authPlayerManager.removeAuthPlayer(event.getPlayer());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onCommand(final CommandExecuteEvent event) {
        if (!event.getResult().isAllowed() || !isCommandsRequireAuth) {
            return;
        }

        // Check if it's a player
        if (!(event.getCommandSource() instanceof Player player)) {
            return;
        }

        // Filter only unauthenticated players
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer != null && authPlayer.isLogged()) {
            return;
        }
        Optional<ServerConnection> server = player.getCurrentServer();
        if (server.isEmpty()) return;
        // Only in auth servers
        if (!isAuthServer(server.get().getServerInfo())) {
            return;
        }
        // Check if command is whitelisted command
        if (commandWhitelist.contains("/" + event.getCommand().split(" ")[0].toLowerCase())) {
            return;
        }
        event.setResult(CommandExecuteEvent.CommandResult.denied());
    }

    // Priority is set to lowest to keep compatibility with some chat plugins
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(final PlayerChatEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        final Player player = event.getPlayer();

        // Filter only unauthenticated players
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer != null && authPlayer.isLogged()) {
            return;
        }
        // Only in auth servers
        Optional<ServerConnection> srv = player.getCurrentServer();
        if (srv.isEmpty()) return;
        if (!isAuthServer(srv.get().getServerInfo())) {
            return;
        }

        if (!chatRequiresAuth) {
            return;
        }
        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    private boolean isAuthServer(ServerInfo serverInfo) {
        return allServersAreAuthServers || authServers.contains(serverInfo.getName().toLowerCase());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerConnectedToServer(final ServerPostConnectEvent event) {
        final Player player = event.getPlayer();
        final Optional<ServerConnection> serverOP = player.getCurrentServer();
        if (serverOP.isEmpty()) return;
        ServerConnection server = serverOP.get();
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        final boolean isAuthenticated = authPlayer != null && authPlayer.isLogged();

        if (isAuthenticated && isAuthServer(server.getServerInfo())) {
            // If AutoLogin enabled, notify the server
            if (isAutoLoginEnabled) {
                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AuthMe.v2");
                out.writeUTF("perform.login");
                out.writeUTF(event.getPlayer().getUsername());
                server.sendPluginMessage(MinecraftChannelIdentifier.from("authme:internal"), out.toByteArray());
            }
        }
    }

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerConnectingToServer(final ServerPreConnectEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        final Player player = event.getPlayer();
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        final boolean isAuthenticated = authPlayer != null && authPlayer.isLogged();

        // Skip logged users
        if (isAuthenticated) {
            return;
        }

        // Only check non auth servers
        if (isAuthServer(event.getOriginalServer().getServerInfo())) {
            return;
        }

        // If the player is not logged in and serverSwitchRequiresAuth is enabled, cancel the connection
        if (isServerSwitchRequiresAuth) {
            event.setResult(ServerPreConnectEvent.ServerResult.denied());

            Component reasonMessage = Component.text(requiresAuthKickMessage).color(NamedTextColor.RED);

            // Handle race condition on player join on a misconfigured network
            if (player.getCurrentServer().isEmpty()) {
                player.disconnect(reasonMessage);
            } else {
                player.sendMessage(reasonMessage);
            }
        }
    }
}
