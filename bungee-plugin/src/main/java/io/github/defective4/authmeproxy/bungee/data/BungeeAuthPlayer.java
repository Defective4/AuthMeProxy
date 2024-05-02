package io.github.defective4.authmeproxy.bungee.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.defective4.authmeproxy.common.api.AuthPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeAuthPlayer extends AuthPlayer {

    public BungeeAuthPlayer(String name, boolean isLogged) {
        super(name.toLowerCase(), isLogged);
    }

    public BungeeAuthPlayer(String name) {
        this(name, false);
    }

    public ProxiedPlayer getPlayer() {
        for (ProxiedPlayer current : ProxyServer.getInstance().getPlayers()) {
            if (current.getName().equalsIgnoreCase(getName())) {
                return current;
            }
        }
        return null;
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    protected void sendCommand(String command, String data, Object srv) {
        ProxiedPlayer player = getPlayer();
        if (player != null) {
            ServerInfo server = player.getServer().getInfo();
            if (server != null) {
                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AuthMe.v2");
                out.writeUTF("perform." + command);
                out.writeUTF(player.getName());
                out.writeBoolean(data != null);
                if (data != null) out.writeUTF(data);
                server.sendData("BungeeCord", out.toByteArray(), false);
            }
        } else if (srv instanceof ServerInfo) {
            ServerInfo server = (ServerInfo) srv;
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("AuthMe.v2");
            out.writeUTF("perform." + command);
            out.writeUTF(getName());
            out.writeBoolean(data != null);
            if (data != null) out.writeUTF(data);
            server.sendData("BungeeCord", out.toByteArray(), false);
        }
    }

    public void forceRegister(String password, ServerInfo server) {
        super.forceRegister(password, server);
    }

    public void forceUnregister(ServerInfo server) {
        super.forceUnregister(server);
    }
}
