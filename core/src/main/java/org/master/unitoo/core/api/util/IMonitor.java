/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.util;

/**
 *
 * @author Andrey
 * @param <T>
 */
public interface IMonitor<T extends IMonitorEvent> {

    void resume();

    void pause();

    void cancel();

    boolean paused();

    boolean stopped();

    void addListener(IMonitorListener<T> listener);

    void removeListener(IMonitorListener<T> listener);

    Iterable<IMonitorListener<T>> listeners();

    void update();

    void check();

}
