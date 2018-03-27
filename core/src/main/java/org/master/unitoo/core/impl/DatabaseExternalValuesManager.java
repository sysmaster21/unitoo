/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.base.BaseExternalValueManager;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.server.DatabaseCheckTask;

/**
 *
 * @author Andrey
 * @param <T> класс источника
 * @param <P> класс родителького эелемента
 */
public abstract class DatabaseExternalValuesManager<T, P> extends BaseExternalValueManager<T, P> {

    private final ConcurrentHashMap<T, DatabaseStorage> storages = new ConcurrentHashMap<>();

    protected abstract DatabaseStorage create(T source) throws UnitooException;

    public abstract Object getItemCode(Object item);

    @Override
    public IExternalStorage<T, P> register(T source) throws UnitooException {
        DatabaseStorage storage = create(source);
        storages.put(source, storage);
        return storage;
    }

    @Override
    public void bootComplete() {
        DatabaseCheckTask task = app().component(DatabaseCheckTask.class);
        if (task != null) {
            for (DatabaseStorage storage : storages.values()) {
                task.startCheck(storage);
            }
        } else {
            app().log().warning("No db scanner found: " + DatabaseCheckTask.class.getName());
        }
    }

}
