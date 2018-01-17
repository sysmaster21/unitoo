/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import org.master.unitoo.core.api.IComponent;

/**
 *
 * @author Andrey
 * @param <T>
 */
public class Setting<T> {

    private volatile boolean changed = false;
    private String name;
    private volatile T value;
    private Class<T> dataClass;
    private IComponent component;
    private ServerConfig config;

    public IComponent component() {
        return component;
    }

    public T val() {
        return value;
    }

    public Class<T> type() {
        return dataClass;
    }

    void init(String name, T value, Class<T> clazz, IComponent component, ServerConfig config) {
        this.name = name;
        this.value = value;
        this.dataClass = clazz;
        this.component = component;
        this.config = config;
    }

    void reinit(T value) {
        this.value = value;
        changed = false;
    }

    T pop() {
        changed = false;
        return value;
    }

    public String name() {
        return name;
    }

    protected void val(T value) {
        if (isDynamic()) {
            this.value = value;
        } else {
            config.startChange();
            try {
                this.value = value;
                changed = true;
            } finally {
                config.endChange();
            }
        }
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isChanged() {
        return changed;
    }

}
