package io.github.defective4.authmeproxy.common.config;


import io.github.defective4.authmeproxy.common.annotations.DataFolder;

import javax.inject.Inject;
import java.io.File;

public class ProxySettingsProvider extends SettingsProvider {

    @Inject
    public ProxySettingsProvider(@DataFolder File dataFolder) {
        super(dataFolder, ProxyConfigProperties.class);
    }

}
