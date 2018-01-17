/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.IStoredGlossaryItem;
import org.master.unitoo.core.api.components.IStoredGlossary;

/**
 *
 * @author Andrey
 * @param <T> класс кода элемента
 */
public class StoredGlossaryItem<T> extends GlossaryItem<T> implements IStoredGlossaryItem<T> {

    private volatile boolean changed = false;

    public StoredGlossaryItem(T code, IStoredGlossary<T, ? extends StoredGlossaryItem<T>> parent) {
        super(code, parent);
    }

    @Override
    public void update() {
        try {
            glossary().startUpdate();
            changed = true;
        } finally {
            glossary().endUpdate();
        }
    }

    @Override
    public void commit() {
        changed = false;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public IStoredGlossary<T, ? extends IGlossaryItem<T>> glossary() {
        return (IStoredGlossary<T, ? extends IGlossaryItem<T>>) super.glossary();
    }

}
