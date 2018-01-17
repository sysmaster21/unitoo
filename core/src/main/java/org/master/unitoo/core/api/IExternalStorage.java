/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.errors.StorageCreateException;
import org.master.unitoo.core.errors.StorageFlushException;
import org.master.unitoo.core.errors.StorageLoadException;

/**
 *
 * @author Andrey
 * @param <T> тип данных хранилища
 */
public interface IExternalStorage<T, P> {

    T source();

    void create() throws StorageCreateException;

    void load() throws StorageLoadException;

    Iterable<String> keys();

    boolean hasValue(String name, P parent, Class type);

    Object getValue(String name, P parent, Class type);

    void putValue(String name, Object value, P parent, Class type);

    void flush() throws StorageFlushException;

    void listener(IChangeListener listener);

}
