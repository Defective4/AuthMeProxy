package io.github.defective4.authmeproxy.common.api;

public abstract class AuthPlayer {
    private String name;
    private boolean isLogged;

    public AuthPlayer(String name, boolean isLogged) {
        this.name = name;
        this.isLogged = isLogged;
    }

    public String getName() {
        return name;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public abstract boolean isOnline();

    public void forceLogin() {
        sendCommand("login", null);
    }

    public void forceLogout() {
        sendCommand("logout", null);
    }

    public void forceRegister(String password) {
        sendCommand("register", password);
    }

    protected void forceRegister(String password, Object server) {
        sendCommand("register", password, server);
    }

    public void forceUnregister() {
        sendCommand("unregister", null);
    }

    protected void forceUnregister(Object server) {
        sendCommand("unregister", null, server);
    }

    protected abstract void sendCommand(String command, String data, Object server);

    protected void sendCommand(String command, String data) {
        sendCommand(command, data, null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "name='" + name + '\'' +
               ", isLogged=" + isLogged +
               '}';
    }
}
