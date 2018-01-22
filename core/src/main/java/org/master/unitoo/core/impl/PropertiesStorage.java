/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.util.IMonitor;
import org.master.unitoo.core.api.util.IMonitorListener;
import org.master.unitoo.core.errors.StorageCreateException;
import org.master.unitoo.core.errors.StorageFlushException;
import org.master.unitoo.core.errors.StorageLoadException;
import org.master.unitoo.core.server.FileCheckTask;
import org.master.unitoo.core.types.CRUD;

/**
 *
 * @author Andrey
 * @param <T> класс источника
 * @param <P> класс родителького эелемента
 * @param <M> класс менеджера хранилища
 */
public abstract class PropertiesStorage<T, P, M extends PropertiesExternalValuesManager> implements IExternalStorage<T, P>, IMonitorListener<FileMonitorEvent> {

    protected static final String NULL = "<--null-->";
    protected static final String ATTR_DELIMITER = "$";

    private final File file;
    private final M manager;
    private final T source;
    private Properties props;
    private IMonitor<FileMonitorEvent> monitor;
    private IChangeListener listener;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public PropertiesStorage(File folder, M parent, T source) {
        this.manager = parent;
        this.source = source;
        this.file = fileName() == null ? folder : new File(folder, fileName());
    }

    public M manager() {
        return manager;
    }

    protected String keyOf(String name, P parent, Class type) {
        String key;
        if (parent == null) {
            key = name;
        } else {
            key = manager().app().format(manager.getItemCode(parent)) + ATTR_DELIMITER + name;
        }
        return key;
    }

    @Override
    public boolean hasValue(String name, P parent, Class type) {
        return props.containsKey(keyOf(name, parent, type));
    }

    @Override
    public Object getValue(String name, P parent, Class type) {
        String value = props.getProperty(keyOf(name, parent, type));
        return NULL.equals(value) ? null : value;
    }

    @Override
    public void putValue(String name, Object value, P parent, Class type) {
        if (value == null) {
            props.setProperty(keyOf(name, parent, type), NULL);
        } else {
            props.setProperty(keyOf(name, parent, type), this.manager.app().format(value));
        }
    }

    @Override
    public void create() throws StorageCreateException {
        try {
            props = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<>(super.keySet()));
                }
            };
            if (file.exists()) {
                try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            throw new StorageCreateException(file == null ? "[null]" : file.getPath(), e);
        }
    }

    @Override
    public void load() throws StorageLoadException {
        try {
            create();
        } catch (IOException e) {
            throw new StorageLoadException(file == null ? "[null]" : file.getPath(), e);
        }
    }

    public abstract String comments();

    @Override
    public void flush() throws StorageFlushException {
        try {
            try (OutputStream in = new BufferedOutputStream(new FileOutputStream(file))) {
                props.store(in, comments());
            }
        } catch (IOException e) {
            throw new StorageFlushException(file.getPath(), e);
        }

        if (monitor != null) {
            monitor.update();
            monitor.resume();
        }
    }

    public void doMonitor(FileCheckTask task) {
        monitor = task.startCheck(file);
        monitor.addListener(this);
    }

    @Override
    public T source() {
        return source;
    }

    @Override
    public Iterable<String> keys() {
        ArrayList<String> keys = new ArrayList<>();
        for (String prop : props.stringPropertyNames()) {
            if (!prop.contains(ATTR_DELIMITER)) {
                int c = prop.lastIndexOf(".");
                keys.add(prop.substring(c + 1));
            }
        }
        return keys;
    }

    protected abstract String fileName();

    @Override
    public void onEvent(FileMonitorEvent event) {
        if (listener != null) {
            if (event.getOperation() == CRUD.Create || event.getOperation() == CRUD.Update) {
                if (event.getFileName().endsWith(fileName())) {
                    monitor.pause();
                    listener.onChanged();
                }
            }
        }
    }

    @Override
    public void listener(IChangeListener listener) {
        this.listener = listener;
    }

}
