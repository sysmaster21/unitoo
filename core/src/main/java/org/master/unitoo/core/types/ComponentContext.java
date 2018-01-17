/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import org.master.unitoo.core.server.ServerConfig;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.components.ILoggerFactory;

/**
 *
 * @author Andrey
 */
public class ComponentContext {

    private final String version;
    private final String descr;
    private final String info;
    private final String url;
    private final IApplication application;
    private final ServerConfig config;
    private final Class<? extends ILoggerFactory> logger;
    private final Class componentClass;
    private final IBootInfo boot;

    public ComponentContext(String descr, String version, String info, String url, IApplication application, ServerConfig config, Class<? extends ILoggerFactory> logger, IBootInfo boot, Class componentClass) {
        this.version = version;
        this.descr = descr;
        this.info = info;
        this.url = url;
        this.application = application;
        this.config = config;
        this.logger = logger;
        this.boot = boot;
        this.componentClass = componentClass;
    }

    public String description() {
        return descr;
    }

    public String version() {
        return version;
    }

    public String url() {
        return url;
    }

    public IApplication application() {
        return application;
    }

    public Class<? extends ILoggerFactory> logger() {
        return logger;
    }

    public ServerConfig config() {
        return config;
    }

    public String info() {
        return info;
    }

    public IBootInfo boot() {
        return boot;
    }

    public Class componentClass() {
        return componentClass;
    }

}
