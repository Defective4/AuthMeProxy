package io.github.defective4.authmeproxy.bukkit;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Locale;

public class BukkitBridge extends JavaPlugin implements Listener {

    private static final String AUTH_CHANNEL = "authme:internal";
    private AuthMeApi authMe;

    @Override
    public void onEnable() {
        authMe = AuthMeApi.getInstance();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, AUTH_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, AUTH_CHANNEL, (channel, player, bytes) -> {
            if (AUTH_CHANNEL.equals(channel))
                try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
                    if (in.readUTF().equals("AuthMe.v2") && in.readUTF().equals("perform.login")) {
                        Player toLogin = Bukkit.getPlayerExact(in.readUTF());
                        if (toLogin != null) {
                            authMe.forceLogin(toLogin);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        sendAuthMeCommand("login", e.getPlayer());
    }

    @EventHandler
    public void onLogout(LogoutEvent e) {
        sendAuthMeCommand("logout", e.getPlayer());
    }

    private void sendAuthMeCommand(String cmd, Player player) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(buffer);
            data.writeUTF("AuthMe.v2.Broadcast");
            data.writeUTF(cmd);
            data.writeUTF(player.getName().toLowerCase(Locale.ROOT));
            Bukkit.getServer().sendPluginMessage(this, AUTH_CHANNEL, buffer.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
