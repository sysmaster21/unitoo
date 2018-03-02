/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import javax.servlet.http.HttpServletResponse;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.errors.MethodFailed;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public abstract class BaseErrorHandler implements IErrorHandler {

    private ComponentContext context;

    @Override
    public ComponentType type() {
        return ComponentType.Formatter;
    }

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
    public String internal() {
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

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void flush(IControllerMethod method, String mapping, HttpServletResponse response, IFormatter formatter, Throwable error) {
        try {
            response.setContentType(content().contentType(formatter.encoding()).toString());
            flushError(method, mapping, response, formatter, error);
        } catch (Throwable t) {
            app().log().error(new MethodFailed(mapping, t));
        }
    }

    protected abstract IDataContent content();

    protected abstract void flushError(IControllerMethod method, String mapping, HttpServletResponse response, IFormatter formatter, Throwable error) throws Exception;

}
