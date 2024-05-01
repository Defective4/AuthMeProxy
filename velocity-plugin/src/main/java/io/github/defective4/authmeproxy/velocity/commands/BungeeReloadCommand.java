package io.github.defective4.authmeproxy.velocity.commands;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.factory.SingletonStore;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import io.github.defective4.authmeproxy.common.config.SettingsDependent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.inject.Inject;

public class BungeeReloadCommand implements SimpleCommand {

    private SettingsManager settings;
    private SingletonStore<SettingsDependent> settingsDependentStore;

    @Inject
    public BungeeReloadCommand(SettingsManager settings, SingletonStore<SettingsDependent> settingsDependentStore) {
        this.settings = settings;
        this.settingsDependentStore = settingsDependentStore;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSender = invocation.source();
        settings.reload();
        settingsDependentStore.retrieveAllOfType().forEach(settingsDependent -> settingsDependent.reload(settings));
        commandSender.sendMessage(Component.text("AuthMeBungee configuration reloaded!").color(NamedTextColor.GREEN));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("authmebungee.reload");
    }
}
