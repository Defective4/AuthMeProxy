package io.github.defective4.authmeproxy.velocity.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github.defective4.authmeproxy.common.config.ProxyConfigProperties;
import io.github.defective4.authmeproxy.common.config.SettingsDependent;
import io.github.defective4.authmeproxy.velocity.AuthMeVelocity;
import io.github.defective4.authmeproxy.velocity.data.VelocityAuthPlayer;
import io.github.defective4.authmeproxy.velocity.services.VelocityAuthPlayerManager;

import javax.inject.Inject;

public class VelocityMessageListener implements SettingsDependent {

    // Services
    private final VelocityAuthPlayerManager authPlayerManager;

    // Settings
    private boolean isSendOnLogoutEnabled;
    private String sendOnLogoutTarget;

    @Inject
    public VelocityMessageListener(final SettingsManager settings, final VelocityAuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(final SettingsManager settings) {
        isSendOnLogoutEnabled = settings.getProperty(ProxyConfigProperties.ENABLE_SEND_ON_LOGOUT);
        sendOnLogoutTarget = settings.getProperty(ProxyConfigProperties.SEND_ON_LOGOUT_TARGET);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }

        // Check if the message is for a server (ignore client messages)
        if (!event.getIdentifier().getId().equals("authme:internal")) {
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        // Check if a player is not trying to send us a fake message
        if (!(event.getSource() instanceof ServerConnection)) {
            if (event.getSource() instanceof Player player) AuthMeVelocity.getInstance()
                                                                          .getLogger()
                                                                          .warn(player.getUsername() + " tried to spoof AuthMe plugin message!");
            return;
        }

        // Read the plugin message
        final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        // Let's check the subchannel
        if (!in.readUTF().equals("AuthMe.v2.Broadcast")) {
            return;
        }

        // For now that's the only type of message the server is able to receive
        final String type = in.readUTF();
        switch (type) {
            case "login":
                handleOnLogin(in);
                break;
            case "logout":
                handleOnLogout(in);
                break;
        }
    }

    private void handleOnLogin(final ByteArrayDataInput in) {
        final String name = in.readUTF();
        final VelocityAuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
        if (authPlayer != null) {
            authPlayer.setLogged(true);
        }
    }

    private void handleOnLogout(final ByteArrayDataInput in) {
        final String name = in.readUTF();
        final VelocityAuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
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
