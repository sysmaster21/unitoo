/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.server.FileCheckTask;
import org.master.unitoo.core.base.BaseExternalValueManager;

/**
 *
 * @author Andrey
 * @param <T> класс источника
 * @param <P> класс родителького эелемента
 */
public abstract class DatabaseExternalValuesManager<T, P> extends BaseExternalValueManager<T, P> {

    private final ConcurrentHashMap<T, PropertiesStorage> storages = new ConcurrentHashMap<>();

    protected abstract PropertiesStorage create(T source);

    @Override
    public IExternalStorage<T, P> register(T source) {
        PropertiesStorage storage = create(source);
        storages.put(source, storage);
        return storage;
    }

    @Override
    public void bootComplete() {
        FileCheckTask task = app().component(FileCheckTask.class);
        if (task != null) {
            for (PropertiesStorage storage : storages.values()) {
                storage.doMonitor(task);
            }
        } else {
            app().log().warning("No file scanner found: " + FileCheckTask.class.getName());
        }
    }

}
