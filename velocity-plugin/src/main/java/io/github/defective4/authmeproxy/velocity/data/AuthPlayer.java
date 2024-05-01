package io.github.defective4.authmeproxy.velocity.data;

import com.velocitypowered.api.proxy.Player;
import io.github.defective4.authmeproxy.velocity.AuthMeVelocity;

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

    public Player getPlayer() {
        for (Player current : AuthMeVelocity.getProxyServer().getAllPlayers()) {
            if (current.getUsername().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

}
