/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IFlushable;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 * @param <C> - класс кода элемента справочника
 * @param <T> - класс элемента справочника
 */
public interface IStoredGlossary<C, T extends IGlossaryItem<C>> extends IGlossary<C, T>, IFlushable {

    void init(IGlossaryManager manager) throws UnitooException;

    void update();

    void startUpdate();

    void endUpdate();

}
