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
public interface IMonitorListener<T extends IMonitorEvent> {

    void onEvent(T event);

}
