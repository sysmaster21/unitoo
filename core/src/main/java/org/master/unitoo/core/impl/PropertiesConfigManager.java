/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.File;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.annotation.GlobalParam;
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.server.ServerConfig;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("config-props")
public class PropertiesConfigManager extends PropertiesExternalValuesManager<ServerConfig, Object> implements IConfigManager {

    @GlobalParam("config.file")
    private File file;

    @Override
    protected ConfigProps create(ServerConfig source) {
        return new ConfigProps(file, this, source);
    }

    @Override
    public ComponentType type() {
        return ComponentType.ConfigManager;
    }

    protected static class ConfigProps extends PropertiesStorage<ServerConfig, Object, PropertiesConfigManager> {

        public ConfigProps(File folder, PropertiesConfigManager parent, ServerConfig source) {
            super(folder, parent, source);
        }

        @Override
        public String comments() {
            return "Unitoo server config file";
        }

        @Override
        protected String fileName() {
            return null;
        }

    }
}
