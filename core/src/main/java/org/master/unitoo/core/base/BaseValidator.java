/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 * @param <T>
 */
public abstract class BaseValidator<T> implements IValidator<T> {

    private ComponentContext context;
    private ILogger logger;

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
    public ComponentType type() {
        return ComponentType.Validator;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public ILogger log() {
        if (logger == null) {
            logger = app().log(context.logger(), getClass());
        }
        return logger;
    }

}
