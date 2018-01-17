/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.master.unitoo.core.api.util.IMonitor;
import org.master.unitoo.core.api.util.IMonitorEvent;
import org.master.unitoo.core.api.util.IMonitorListener;

/**
 *
 * @author Andrey
 * @param <T>
 */
public abstract class AbstractMonitor<T extends IMonitorEvent> implements IMonitor<T> {

    private final static int STATE_ACTIVE = 0;
    private final static int STATE_PAUSED = 1;
    private final static int STATE_STOPPED = 2;
    private final AtomicInteger state = new AtomicInteger(STATE_ACTIVE);
    private final ConcurrentHashMap<IMonitorListener<T>, IMonitorListener<T>> listeners = new ConcurrentHashMap();

    @Override
    public void resume() {
        state.set(STATE_ACTIVE);
    }

    @Override
    public void pause() {
        state.set(STATE_PAUSED);
    }

    @Override
    public boolean paused() {
        return state.get() == STATE_PAUSED;
    }

    @Override
    public void cancel() {
        state.set(STATE_STOPPED);
    }

    @Override
    public boolean stopped() {
        return state.get() == STATE_STOPPED;
    }

    @Override
    public void addListener(IMonitorListener<T> listener) {
        listeners.put(listener, listener);
    }

    @Override
    public void removeListener(IMonitorListener<T> listener) {
        listeners.remove(listener);
    }

    @Override
    public Iterable<IMonitorListener<T>> listeners() {
        return listeners.values();
    }

}
