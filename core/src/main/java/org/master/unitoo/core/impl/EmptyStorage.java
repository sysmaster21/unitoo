/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.ArrayList;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.errors.StorageCreateException;
import org.master.unitoo.core.errors.StorageFlushException;
import org.master.unitoo.core.errors.StorageLoadException;

/**
 *
 * @author Andrey
 */
public class EmptyStorage<T, P> implements IExternalStorage<T, P> {

    private final T source;

    public EmptyStorage(T source) {
        this.source = source;
    }

    @Override
    public T source() {
        return source;
    }

    @Override
    public Iterable<String> keys() {
        return new ArrayList<>();
    }

    @Override
    public void create() throws StorageCreateException {
    }

    @Override
    public void load() throws StorageLoadException {
    }

    @Override
    public boolean hasValue(String name, P parent, Class type) {
        return false;
    }

    @Override
    public Object getValue(String name, P parent, Class type) {
        return null;
    }

    @Override
    public void putValue(String name, Object value, P parent, Class type) {
    }

    @Override
    public void flush() throws StorageFlushException {
    }

    @Override
    public void listener(IChangeListener listener) {
    }

}
