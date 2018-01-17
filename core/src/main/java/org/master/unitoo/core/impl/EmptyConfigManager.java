/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.base.BaseExternalValueManager;
import org.master.unitoo.core.server.ServerConfig;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("config-empty")
public class EmptyConfigManager extends BaseExternalValueManager<ServerConfig, Object> implements IConfigManager {

    @Override
    public IExternalStorage<ServerConfig, Object> register(ServerConfig source) {
        return new EmptyStorage<>(source);
    }

    @Override
    public void bootComplete() {
    }

    @Override
    public ComponentType type() {
        return ComponentType.ConfigManager;
    }

}
