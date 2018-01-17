/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IGlossaryItem;

/**
 *
 * @author Andrey
 * @param <C> - класс кода элемента справочника
 * @param <T> - класс элемента справочника
 */
public interface IGlossary<C, T extends IGlossaryItem<C>> extends IComponent, IAutowired {

    Iterable<T> items();

    boolean cached();

    T item(C code);

    String label(C code);

    String label(C code, ILanguage language);
    
    Class<C> codeType();

}
