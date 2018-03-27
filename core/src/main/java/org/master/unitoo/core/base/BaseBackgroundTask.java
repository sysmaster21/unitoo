/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.concurrent.atomic.AtomicReference;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.components.IBackgroundTask;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.api.synthetic.DateTime;
import org.master.unitoo.core.types.MethodType;
import org.master.unitoo.core.types.RunnableState;

/**
 *
 * @author Andrey
 */
public abstract class BaseBackgroundTask implements IBackgroundTask {

    private final AtomicReference<RunnableState> state = new AtomicReference<>(RunnableState.Stopped);
    private long lastExecute = System.currentTimeMillis();
    private long nextExecute = System.currentTimeMillis();
    private long runFrom = 0;
    private long stopFrom = 0;
    private ComponentContext context;
    private ILogger logger;
    private Thread thread;

    protected abstract long calcNext();

    protected abstract boolean startImmediately();

    private boolean changeState(RunnableState newState, RunnableState... fromStates) {
        if (fromStates == null || fromStates.length == 0) {
            state.set(newState);
            return true;
        }

        for (RunnableState curState : fromStates) {
            if (state.compareAndSet(curState, newState)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RunnableState state() {
        return state.get();
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String extKey() {
        return context.internal();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public IApplication app() {
        return context.application();
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
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public long stopping() {
        return System.currentTimeMillis() - stopFrom;
    }

    @Override
    public void stop() {
        stopFrom = System.currentTimeMillis();
        try {
            changeState(RunnableState.Stopping, RunnableState.Running);
            changeState(RunnableState.Stopped, RunnableState.Waiting);
        } catch (Throwable t) {
            log().error(t);
        }
    }

    @Override
    public void kill() {
        try {
            if (changeState(RunnableState.Killing, RunnableState.Running, RunnableState.Stopping)) {
                if (thread != null) {
                    thread.interrupt();
                }
            }
        } catch (Throwable t) {
            log().error(t);
        }
    }

    @Override
    public void init(ComponentContext context) {
        changeState(RunnableState.Init);
        this.context = context;
        changeState(RunnableState.Stopped);
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void destroy() {
        stop();
    }

    @Override
    public ComponentType type() {
        return ComponentType.BackgroundTask;
    }

    @Override
    public DateTime nextRun() {
        return new DateTime(nextExecute);
    }

    @Override
    public DateTime lastRun() {
        return new DateTime(lastExecute);
    }

    @Override
    public long running() {
        return runFrom != 0 ? System.currentTimeMillis() - runFrom : 0;
    }

    @Override
    public void prepare() {
    }

    @Override
    public void reject() {
        log().warning("Task " + name() + " rejected");
    }

    @Override
    public void start() {
        if (changeState(RunnableState.Waiting, RunnableState.Stopped)) {
            if (next(true)) {
                log().info("Task " + name() + " started, next run: " + app().format(nextRun()));
            }
        }
    }

    protected abstract void execute() throws Throwable;

    protected void live(String info) {
        app().process().methodLive(info);
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        if (changeState(RunnableState.Running, RunnableState.Waiting)) {
            app().process().methodEnter(name(), MethodType.Task, "");
            try {
                runFrom = System.currentTimeMillis();
                execute();
                lastExecute = System.currentTimeMillis();
            } catch (InterruptedException e) {
                log().warning("Task has been killed");
                changeState(RunnableState.Stopped, RunnableState.Stopping, RunnableState.Killing);
            } catch (Throwable e) {
                log().error(e);
            } finally {
                app().process().methodExit();
            }

            runFrom = 0;
            changeState(RunnableState.Waiting, RunnableState.Running);
            changeState(RunnableState.Stopped, RunnableState.Stopping, RunnableState.Killing);
            next(false);
        } else {
            changeState(RunnableState.Stopped, RunnableState.Stopping, RunnableState.Killing);
        }
        thread = null;
    }

    private boolean next(boolean firstTime) {
        if (firstTime && startImmediately()) {
            nextExecute = System.currentTimeMillis();
            app().execute(this, 0);
            return true;
        } else {
            long delay = calcNext();
            if (delay != 0) {
                nextExecute = System.currentTimeMillis() + delay;
                app().execute(this, delay);
                return true;
            } else {
                log().warning("Can't start task " + name() + ": delay is zero.");
                changeState(RunnableState.Stopped, RunnableState.Waiting);
            }
        }

        return false;
    }

}
