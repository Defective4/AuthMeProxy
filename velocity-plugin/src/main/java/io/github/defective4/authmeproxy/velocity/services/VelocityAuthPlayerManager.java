package io.github.defective4.authmeproxy.velocity.services;

import com.velocitypowered.api.proxy.Player;
import io.github.defective4.authmeproxy.velocity.data.VelocityAuthPlayer;

import java.util.HashMap;
import java.util.Map;

/*
 * Players manager - store all references to AuthPlayer objects through an HashMap
 */
public class VelocityAuthPlayerManager {

    private Map<String, VelocityAuthPlayer> players;

    public VelocityAuthPlayerManager() {
        players = new HashMap<>();
    }

    public void addAuthPlayer(VelocityAuthPlayer player) {
        players.put(player.getName(), player);
    }

    public void addAuthPlayer(Player player) {
        addAuthPlayer(new VelocityAuthPlayer(player.getUsername().toLowerCase()));
    }

    public void removeAuthPlayer(String name) {
        players.remove(name.toLowerCase());
    }

    public void removeAuthPlayer(Player player) {
        removeAuthPlayer(player.getUsername());
    }

    public VelocityAuthPlayer getAuthPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    public VelocityAuthPlayer getAuthPlayer(Player player) {
        return getAuthPlayer(player.getUsername());
    }
}
