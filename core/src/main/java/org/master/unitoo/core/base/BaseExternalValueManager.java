/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.api.IExternalValuesManager;

/**
 *
 * @author Andrey
 * @param <T> класс источник
 * @param <P> класс родительского эелемента
 */
public abstract class BaseExternalValueManager<T, P> implements IExternalValuesManager<T, P> {

    private ComponentContext context;

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public String extKey() {
        return context.internal();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

}
