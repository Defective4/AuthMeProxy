package io.github.defective4.authmeproxy.velocity.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSink;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github.defective4.authmeproxy.common.api.AuthPlayer;
import io.github.defective4.authmeproxy.velocity.AuthMeVelocity;

public class VelocityAuthPlayer extends AuthPlayer {
    public VelocityAuthPlayer(String name, boolean isLogged) {
        super(name.toLowerCase(), isLogged);
    }

    public VelocityAuthPlayer(String name) {
        this(name, false);
    }

    public Player getPlayer() {
        for (Player current : AuthMeVelocity.getProxyServer().getAllPlayers()) {
            if (current.getUsername().equalsIgnoreCase(getName())) {
                return current;
            }
        }
        return null;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    protected void sendCommand(String command, String data, Object srv) {
        Player player = getPlayer();
        if (player != null) {
            player.getCurrentServer().ifPresent(server -> {
                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AuthMe.v2");
                out.writeUTF("perform." + command);
                out.writeUTF(player.getUsername());
                out.writeBoolean(data != null);
                if (data != null) out.writeUTF(data);
                server.sendPluginMessage(MinecraftChannelIdentifier.from("authme:internal"), out.toByteArray());
            });
        } else if (srv instanceof ChannelMessageSink server) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("AuthMe.v2");
            out.writeUTF("perform." + command);
            out.writeUTF(getName());
            out.writeBoolean(data != null);
            if (data != null) out.writeUTF(data);
            server.sendPluginMessage(MinecraftChannelIdentifier.from("authme:internal"), out.toByteArray());
        }
    }

    public void forceRegister(String password, RegisteredServer server) {
        super.forceRegister(password, server);
    }

    public void forceUnregister(RegisteredServer server) {
        super.forceUnregister(server);
    }

    public void forceRegister(String password, ServerConnection server) {
        super.forceRegister(password, server);
    }

    public void forceUnregister(ServerConnection server) {
        super.forceUnregister(server);
    }

}
