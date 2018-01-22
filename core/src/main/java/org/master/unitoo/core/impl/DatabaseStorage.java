/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Date;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IDatabaseStorageAPI;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.util.IMonitorListener;
import org.master.unitoo.core.errors.DatabaseException;
import org.master.unitoo.core.errors.StorageAccessException;
import org.master.unitoo.core.errors.StorageCreateException;
import org.master.unitoo.core.errors.StorageFlushException;
import org.master.unitoo.core.errors.StorageLoadException;

/**
 *
 * @author Andrey
 * @param <T> класс источника
 * @param <P> класс родителького эелемента
 * @param <M> класс менеджера хранилища
 */
public abstract class DatabaseStorage<T, P, M extends PropertiesExternalValuesManager> implements IExternalStorage<T, P>, IMonitorListener<FileMonitorEvent> {

    private final String name;
    private final M manager;
    private final T source;
    private final IDatabaseStorageAPI api;
    private IChangeListener listener;
    private Date date;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DatabaseStorage(String name, M parent, T source, IDatabaseStorageAPI api) {
        this.manager = parent;
        this.source = source;
        this.name = name;
        this.api = api;
    }

    public M manager() {
        return manager;
    }

    @Override
    public boolean hasValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            Integer v;
            if (parent == null) {
                v = api.containsKey(this.name, name);
            } else {
                v = api.containsAttr(this.name, manager.app().format(manager.getItemCode(parent)), name);
            }
            return v != null && v > 0;
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    @Override
    public Object getValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            if (parent == null) {
                return api.getValue(this.name, name);
            } else {
                return api.getAttr(this.name, manager.app().format(manager.getItemCode(parent)), name);
            }
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    @Override
    public void putValue(String name, Object value, P parent, Class type) throws StorageAccessException {
        try {
            boolean present = hasValue(name, parent, type);
            if (parent == null) {
                if (present) {
                    api.addValue(this.name, name, value);
                } else {
                    api.setValue(this.name, name, value);
                }
            } else {
                if (present) {
                    api.addAttr(this.name, manager.app().format(manager.getItemCode(parent)), name, value);
                } else {
                    api.setAttr(this.name, manager.app().format(manager.getItemCode(parent)), name, value);
                }
            }
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    @Override
    public void create() throws StorageCreateException {
    }

    @Override
    public void load() throws StorageLoadException {
    }

    @Override
    public void flush() throws StorageFlushException {
        date = new Date();
    }

    @Override
    public T source() {
        return source;
    }

    @Override
    public Iterable<String> keys() throws StorageAccessException {
        try {
            return api.keys(name);
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    public void checkChanges() {
        try {
            Integer cnt = api.changedAfter(name, date);
            if (cnt != null && cnt > 0) {
                listener.onChanged();
            }
        } catch (DatabaseException e) {
            manager.app().log().error(e);
        }
    }

    @Override
    public void listener(IChangeListener listener) {
        this.listener = listener;
    }

}
