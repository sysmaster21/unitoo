/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 * @param <T> класс источника для хранилища
 * @param <P> класс для родителького 
 */
public interface IExternalValuesManager<T, P> extends IComponent, IBootListener {

    IExternalStorage<T, P> register(T source) throws UnitooException;

}
