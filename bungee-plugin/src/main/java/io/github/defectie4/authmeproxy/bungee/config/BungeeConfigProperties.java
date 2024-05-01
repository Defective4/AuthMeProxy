package io.github.defectie4.authmeproxy.bungee.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class BungeeConfigProperties implements SettingsHolder {

    @Comment("List of servers in the network where authme is installed")
    public static final Property<List<String>> AUTH_SERVERS =
        newListProperty("authServers", "lobby");
    @Comment("Consider every server as an auth server")
    public static final Property<Boolean> ALL_SERVERS_ARE_AUTH_SERVERS =
        newProperty("allServersAreAuthServers", false);
    @Comment("Allows or not commands to be performed if user is not logged in")
    public static final Property<Boolean> COMMANDS_REQUIRE_AUTH =
        newProperty("commands.requireAuth", true);
    @Comment("List of commands allowed to be perform without being authenticated")
    public static final Property<List<String>> COMMANDS_WHITELIST =
        newListProperty("commands.whitelist", "/login", "/register", "/l", "/reg", "/email", "/captcha", "/2fa", "/totp", "/log");
    @Comment("Allows or not user to talk in chat if he is not logged in")
    public static final Property<Boolean> CHAT_REQUIRES_AUTH =
        newProperty("chatRequiresAuth", true);
    @Comment("Kick all players who switch servers without being authenticated (eg. plugin teleport)")
    public static final Property<Boolean> SERVER_SWITCH_REQUIRES_AUTH =
        newProperty("serverSwitch.requiresAuth", true);
    public static final Property<String> SERVER_SWITCH_KICK_MESSAGE =
        newProperty("serverSwitch.kickMessage", "Authentication required.");
    @Comment("Enable auto-login between servers")
    public static final Property<Boolean> AUTOLOGIN =
        newProperty("autoLogin", false);
    @Comment("If enabled, unlogged users will be sent to the unloggedUserServer server!")
    public static final Property<Boolean> ENABLE_SEND_ON_LOGOUT =
        newProperty("sendOnLogout", false);
    @Comment("If sendOnLogout is enabled, unlogged users will be sent to this server!")
    public static final Property<String> SEND_ON_LOGOUT_TARGET =
        newProperty("unloggedUserServer", "");

    private BungeeConfigProperties() {
    }

}
