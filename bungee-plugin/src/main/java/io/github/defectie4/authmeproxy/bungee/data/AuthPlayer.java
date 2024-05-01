package io.github.defectie4.authmeproxy.bungee.data;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AuthPlayer {

    private String name;
    private boolean isLogged;

    public AuthPlayer(String name, boolean isLogged) {
        this.name = name.toLowerCase();
        this.isLogged = isLogged;
    }

    public AuthPlayer(String name) {
        this(name, false);
    }

    public String getName() {
        return name;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public ProxiedPlayer getPlayer() {
        for (ProxiedPlayer current : ProxyServer.getInstance().getPlayers()) {
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

}
