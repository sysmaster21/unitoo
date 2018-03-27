/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.master.sqlonfly.interfaces.ILogic;
import org.master.sqlonfly.interfaces.IReturnableLogic;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IInterfacedComponent;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.components.IService;
import org.master.unitoo.core.errors.DatabaseException;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.RunnableState;
import org.master.unitoo.core.utils.ServiceWrapper;

/**
 *
 * @author Andrey
 */
public class BaseService implements IService {

    private ComponentContext context;
    private ILogger logger;
    private volatile RunnableState state = RunnableState.Stopped;
    private final AtomicInteger runCount = new AtomicInteger(0);
    private long stopFrom = 0;
    private final ArrayList<Class<? extends IInterfacedComponent>> interfaces = new ArrayList<>();

    public BaseService() {
        scanInterfaces(getClass());
    }

    public void transation(ILogic logic) throws DatabaseException {
        try {
            app().sql().transaction(logic);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public <T> T transation(IReturnableLogic<T> logic) throws DatabaseException {
        try {
            return app().sql().transaction(logic);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public RunnableState state() {
        return state == RunnableState.Stopped && runCount.get() != 0 ? RunnableState.Stopping : state;
    }

    @Override
    public ComponentType type() {
        return ComponentType.Service;
    }

    @Override
    public ILogger log() {
        if (logger == null) {
            logger = app().log(context.logger(), getClass());
        }
        return logger;
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public void init(ComponentContext context) {
        state = RunnableState.Init;
        this.context = context;
    }

    @Override
    public void destroy() {
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public void prepare() {
    }

    @Override
    public void start() {
        state = RunnableState.Running;
    }

    @Override
    public long stopping() {
        return System.currentTimeMillis() - stopFrom;
    }

    @Override
    public void stop() {
        stopFrom = System.currentTimeMillis();
        state = RunnableState.Stopped;
    }

    @Override
    public void kill() {
        //NO THREADS TO STOP
    }

    @Override
    public void reject() {
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
    public String extKey() {
        return context.internal();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    protected void live(String info) {
        app().process().methodLive(info);
    }

    @Override
    public <T> T wrapper(Class<T> iface) {
        Class[] ifaces = new Class[]{iface};
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), ifaces, new ServiceWrapper(this));
    }

    @Override
    public Iterable<Class<? extends IInterfacedComponent>> interfaces() {
        return interfaces;
    }

    private void scanInterfaces(Class clazz) {
        if (clazz.getSuperclass() != null) {
            scanInterfaces(clazz.getSuperclass());
        }

        if (IInterfacedComponent.class.isAssignableFrom(clazz)) {
            for (Class iface : clazz.getInterfaces()) {
                if (IInterfacedComponent.class.isAssignableFrom(iface) && iface != IService.class) {
                    interfaces.add(iface);
                }
            }
        }
    }

}
