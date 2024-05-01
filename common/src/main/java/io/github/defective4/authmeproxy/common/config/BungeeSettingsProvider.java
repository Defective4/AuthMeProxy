package io.github.defective4.authmeproxy.common.config;


import io.github.defective4.authmeproxy.common.annotations.DataFolder;

import javax.inject.Inject;
import java.io.File;

public class BungeeSettingsProvider extends SettingsProvider {

    @Inject
    public BungeeSettingsProvider(@DataFolder File dataFolder) {
        super(dataFolder, BungeeConfigProperties.class);
    }

}
