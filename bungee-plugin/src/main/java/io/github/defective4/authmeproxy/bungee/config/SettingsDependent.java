package io.github.defective4.authmeproxy.bungee.config;

import ch.jalu.configme.SettingsManager;

/**
 * Interface for classes that keep a local copy of certain settings.
 */
public interface SettingsDependent {

    /**
     * Performs a reload with the provided settings instance.
     *
     * @param settings the settings instance
     */
    void reload(SettingsManager settings);

}
