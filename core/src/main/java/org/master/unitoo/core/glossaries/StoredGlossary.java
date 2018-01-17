/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.IStoredGlossaryItem;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.IStoredGlossary;
import org.master.unitoo.core.base.BaseGlossary;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.server.FlushCheckTask;
import org.master.unitoo.core.types.CustomAttribute;

/**
 *
 * @author Andrey
 * @param <C> - класс кода элемента справочника
 * @param <T> - класс элемента справочника
 */
public abstract class StoredGlossary<C, T extends IStoredGlossaryItem<C>> extends BaseGlossary<C, T> implements IStoredGlossary<C, T>, IChangeListener {

    private IExternalStorage<IStoredGlossary, IGlossaryItem> storage;
    private volatile boolean changed = false;

    @Override
    public void init(IGlossaryManager manager) throws UnitooException {
        storage = manager.register(this);
        storage.listener(this);
        loadNow();
    }

    @Override
    public void onChanged() {
        loadNow();
    }

    @Override
    public void update() {
        startLock();
        try {
            changed = true;
        } finally {
            endLock();
        }
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void startUpdate() {
        startLock();
    }

    @Override
    public void endUpdate() {
        endLock();
    }

    protected abstract T createItem(C code);

    @Override
    @SuppressWarnings("UseSpecificCatch")
    protected void load(GlossaryLoader<T> loader) {
        startLock();
        try {
            storage.load();
            for (String key : storage.keys()) {
                C code = (C) app().convert(storage.getValue(key, null, IGlossaryItem.class), codeType());
                T item = createItem(code);
                for (CustomAttribute attr : item.attributes()) {
                    if (storage.hasValue(attr.name(), item, CustomAttribute.class)) {
                        attr.value(app().convert(storage.getValue(attr.name(), item, CustomAttribute.class), attr.type()));
                    }
                }
                loader.add(item);
            }
            storage.flush();
        } catch (Throwable t) {
            app().log().error("Failed to load glossary: " + name(), t);
        } finally {
            endLock();
        }
    }

    @Override
    public void flush() throws UnitooException {
        startLock();
        try {
            storage.create();
            for (T item : items()) {
                if (item.isChanged()) {
                    storage.putValue(app().format(item.code()), item.code(), null, IGlossaryItem.class);
                    for (CustomAttribute attr : item.attributes()) {
                        storage.putValue(attr.name(), attr.value(), item, CustomAttribute.class);
                    }
                }
            }
            storage.flush();
            changed = false;
        } finally {
            endLock();
        }
    }

    @Override
    public void bootComplete() {
        FlushCheckTask task = app().component(FlushCheckTask.class);
        if (task != null) {
            task.startCheck(this);
        }
    }

}
