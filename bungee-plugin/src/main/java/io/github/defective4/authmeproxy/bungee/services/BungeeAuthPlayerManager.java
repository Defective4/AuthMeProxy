package io.github.defective4.authmeproxy.bungee.services;

import io.github.defective4.authmeproxy.bungee.data.BungeeAuthPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

/*
 * Players manager - store all references to AuthPlayer objects through an HashMap
 */
public class BungeeAuthPlayerManager {

    private Map<String, BungeeAuthPlayer> players;

    public BungeeAuthPlayerManager() {
        players = new HashMap<>();
    }

    public void addAuthPlayer(BungeeAuthPlayer player) {
        players.put(player.getName(), player);
    }

    public void addAuthPlayer(ProxiedPlayer player) {
        addAuthPlayer(new BungeeAuthPlayer(player.getName().toLowerCase()));
    }

    public void removeAuthPlayer(String name) {
        players.remove(name.toLowerCase());
    }

    public void removeAuthPlayer(ProxiedPlayer player) {
        removeAuthPlayer(player.getName());
    }

    public BungeeAuthPlayer getAuthPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    public BungeeAuthPlayer getAuthPlayer(ProxiedPlayer player) {
        return getAuthPlayer(player.getName());
    }
}
