package io.github.defective4.authmeproxy.velocity.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.defective4.authmeproxy.common.config.BungeeConfigProperties;
import io.github.defective4.authmeproxy.common.config.SettingsDependent;
import io.github.defective4.authmeproxy.velocity.AuthMeVelocity;
import io.github.defective4.authmeproxy.velocity.data.AuthPlayer;
import io.github.defective4.authmeproxy.velocity.services.AuthPlayerManager;

import javax.inject.Inject;

public class BungeeMessageListener implements SettingsDependent {

    // Services
    private final AuthPlayerManager authPlayerManager;

    // Settings
    private boolean isSendOnLogoutEnabled;
    private String sendOnLogoutTarget;

    @Inject
    public BungeeMessageListener(final SettingsManager settings, final AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(final SettingsManager settings) {
        isSendOnLogoutEnabled = settings.getProperty(BungeeConfigProperties.ENABLE_SEND_ON_LOGOUT);
        sendOnLogoutTarget = settings.getProperty(BungeeConfigProperties.SEND_ON_LOGOUT_TARGET);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        System.out.println(event.getIdentifier().getId());
//        if (!event.getResult().isAllowed()) {
//            return;
//        }

        // Check if the message is for a server (ignore client messages)
        if (!event.getIdentifier().getId().equals("BungeeCord")) {
            return;
        }

        // Check if a player is not trying to send us a fake message
        if (!(event.getSource() instanceof ProxyServer)) {
            return;
        }

        // Read the plugin message
        final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        // Accept only broadcasts
        if (!in.readUTF().equals("Forward")) {
            return;
        }
        in.readUTF(); // Skip ONLINE/ALL parameter

        // Let's check the subchannel
        if (!in.readUTF().equals("AuthMe.v2.Broadcast")) {
            return;
        }

        // Read data byte array
        final short dataLength = in.readShort();
        final byte[] dataBytes = new byte[dataLength];
        in.readFully(dataBytes);
        final ByteArrayDataInput dataIn = ByteStreams.newDataInput(dataBytes);

        // For now that's the only type of message the server is able to receive
        final String type = dataIn.readUTF();
        switch (type) {
            case "login":
                handleOnLogin(dataIn);
                break;
            case "logout":
                handleOnLogout(dataIn);
                break;
        }
    }

    private void handleOnLogin(final ByteArrayDataInput in) {
        final String name = in.readUTF();
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
        if (authPlayer != null) {
            authPlayer.setLogged(true);
        }
    }

    private void handleOnLogout(final ByteArrayDataInput in) {
        final String name = in.readUTF();
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
        if (authPlayer != null) {
            authPlayer.setLogged(false);
            if (isSendOnLogoutEnabled) {
                final Player player = authPlayer.getPlayer();
                if (player != null) {
                    AuthMeVelocity.getProxyServer()
                                  .getServer(sendOnLogoutTarget)
                                  .ifPresent(player::createConnectionRequest);
                }
            }
        }
    }

}
