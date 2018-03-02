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
import org.master.unitoo.core.errors.TypeConvertExpection;

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

    public DatabaseStorage(String name, M parent, T source) {
        this.manager = parent;
        this.source = source;
        this.name = name;
    }

    public M manager() {
        return manager;
    }

    protected abstract Integer containsAttr(Object code, String attrName) throws DatabaseException;

    protected abstract Integer containsKey(String code) throws DatabaseException;

    @Override
    public boolean hasValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            Integer v;
            if (parent == null) {
                v = containsKey(name);
            } else {
                v = containsAttr(manager.getItemCode(parent), name);
            }
            return v != null && v > 0;
        } catch (DatabaseException e) {
            throw new StorageAccessException(name, e);
        }
    }

    protected abstract Object getAttr(Object code, String attrName, Class type) throws DatabaseException, TypeConvertExpection;

    protected abstract Object getValue(String code, Class type) throws DatabaseException, TypeConvertExpection;

    @Override
    public Object getValue(String name, P parent, Class type) throws StorageAccessException {
        try {
            if (parent == null) {
                return getValue(name, type);
            } else {
                return getAttr(manager.getItemCode(parent), name, type);
            }
        } catch (DatabaseException | TypeConvertExpection e) {
            throw new StorageAccessException(name, e);
        }
    }

    protected abstract int setAttr(Object code, String attrName, Object value) throws DatabaseException;

    protected abstract int addAttr(Object code, String attrName, Object value) throws DatabaseException;

    protected abstract int setValue(String code, Object value) throws DatabaseException;

    protected abstract int addValue(String code, Object value) throws DatabaseException;

    @Override
    public void putValue(String name, Object value, P parent, Class type) throws StorageAccessException {
        try {
            boolean present = hasValue(name, parent, type);
            if (parent == null) {
                if (present) {
                    setValue(name, value);
                } else {
                    addValue(name, value);
                }
            } else {
                if (present) {
                    setAttr(manager.getItemCode(parent), name, value);
                } else {
                    addAttr(manager.getItemCode(parent), name, value);
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

    protected abstract Integer changedAfter(Date date) throws DatabaseException;

    public void checkChanges() {
        try {
            Integer cnt = changedAfter(date);
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

    public String name() {
        return name;
    }

}
