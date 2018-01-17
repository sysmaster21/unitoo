/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.api.components.IStoredGlossary;

/**
 *
 * @author Andrey
 * @param <T> класс кода элемента
 */
public interface IStoredGlossaryItem<T> extends IGlossaryItem<T> {

    void update();

    void commit();

    boolean isChanged();

    @Override
    IStoredGlossary<T, ? extends IGlossaryItem<T>> glossary();

}
