package io.github.defective4.authmeproxy.bungee.commands;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.factory.SingletonStore;
import io.github.defective4.authmeproxy.common.config.SettingsDependent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import javax.inject.Inject;

public class BungeeReloadCommand extends Command {

    private SettingsManager settings;
    private SingletonStore<SettingsDependent> settingsDependentStore;

    @Inject
    public BungeeReloadCommand(SettingsManager settings, SingletonStore<SettingsDependent> settingsDependentStore) {
        super("abreloadproxy", "authmebungee.reload");
        this.settings = settings;
        this.settingsDependentStore = settingsDependentStore;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        settings.reload();
        settingsDependentStore.retrieveAllOfType().forEach(settingsDependent -> settingsDependent.reload(settings));
        commandSender.sendMessage(
            new ComponentBuilder("AuthMeProxy configuration reloaded!").color(ChatColor.GREEN).create()
        );
    }
}
