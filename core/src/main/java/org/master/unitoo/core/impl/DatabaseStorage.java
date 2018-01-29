/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Date;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
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
public abstract class DatabaseStorage<T, P, M extends DatabaseExternalValuesManager> implements IExternalStorage<T, P> {

    private final String name;
    private final M manager;
    private final T source;
    private IChangeListener listener;
    private Date date;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DatabaseStorage(String name, M parent, T source) {
        this.manager = parent;
        this.source = source;
        this.name = name;
    }

    public M manager() {
        return manager;
    }

    protected abstract Integer containsAttr(String storage, String code, String attrName) throws DatabaseException;

    protected abstract Integer containsKey(String storage, String code) throws DatabaseException;

    @Override
    public boolean hasValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            Integer v;
            if (parent == null) {
                v = containsKey(this.name, name);
            } else {
                v = containsAttr(this.name, manager.app().format(manager.getItemCode(parent)), name);
            }
            return v != null && v > 0;
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    protected abstract String getAttr(String storage, String code, String attrName) throws DatabaseException;

    protected abstract String getValue(String storage, String code) throws DatabaseException;

    @Override
    public Object getValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            if (parent == null) {
                return getValue(this.name, name);
            } else {
                return getAttr(this.name, manager.app().format(manager.getItemCode(parent)), name);
            }
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    protected abstract int setAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    protected abstract int addAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    protected abstract int setValue(String storage, String code, Object value) throws DatabaseException;

    protected abstract int addValue(String storage, String code, Object value) throws DatabaseException;

    @Override
    public void putValue(String name, Object value, P parent, Class type) throws StorageAccessException {
        try {
            boolean present = hasValue(name, parent, type);
            if (parent == null) {
                if (present) {
                    addValue(this.name, name, value);
                } else {
                    setValue(this.name, name, value);
                }
            } else {
                if (present) {
                    addAttr(this.name, manager.app().format(manager.getItemCode(parent)), name, value);
                } else {
                    setAttr(this.name, manager.app().format(manager.getItemCode(parent)), name, value);
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

    protected abstract Iterable<String> keys(String storage) throws DatabaseException;

    @Override
    public Iterable<String> keys() throws StorageAccessException {
        try {
            return keys(name);
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    protected abstract Integer changedAfter(String storage, Date date) throws DatabaseException;

    public void checkChanges() {
        try {
            Integer cnt = changedAfter(name, date);
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
