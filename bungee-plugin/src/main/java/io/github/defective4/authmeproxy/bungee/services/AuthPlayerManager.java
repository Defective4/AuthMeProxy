package io.github.defective4.authmeproxy.bungee.services;

import io.github.defective4.authmeproxy.bungee.data.AuthPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

/*
 * Players manager - store all references to AuthPlayer objects through an HashMap
 */
public class AuthPlayerManager {

    private Map<String, AuthPlayer> players;

    public AuthPlayerManager() {
        players = new HashMap<>();
    }

    public void addAuthPlayer(AuthPlayer player) {
        players.put(player.getName(), player);
    }

    public void addAuthPlayer(ProxiedPlayer player) {
        addAuthPlayer(new AuthPlayer(player.getName().toLowerCase()));
    }

    public void removeAuthPlayer(String name) {
        players.remove(name.toLowerCase());
    }

    public void removeAuthPlayer(ProxiedPlayer player) {
        removeAuthPlayer(player.getName());
    }

    public AuthPlayer getAuthPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    public AuthPlayer getAuthPlayer(ProxiedPlayer player) {
        return getAuthPlayer(player.getName());
    }
}
