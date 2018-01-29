/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.IFlushable;

/**
 *
 * @author Andrey
 */
public class ServerConfig extends ConcurrentHashMap<String, Setting> implements IChangeListener, IFlushable {

    private final ReentrantLock barrier = new ReentrantLock();
    private final IApplication app;
    private Setting[] formatterSettings;
    private IExternalStorage<ServerConfig, Object> storage;
    private volatile boolean changed = false;

    public ServerConfig(IApplication application) {
        this.app = application;
    }

    public <T> void register(Setting<T> setting, String name, T value, Class<T> clazz, IComponent component) {
        if (setting != null) {
            setting.init(name, value, clazz, component, this);
            put(name, setting);
        }
    }

    public <T> void reinit(Setting<T> setting, T value) {
        if (setting != null) {
            Setting check = get(setting.name());
            if (check == setting) {
                setting.reinit(value);
            }
        }
    }

    public void init(IConfigManager manager, Setting[] formatterSettings) throws UnitooException {
        this.formatterSettings = formatterSettings;
        storage = manager.register(this);
        storage.listener(this);
        reload(true);
    }

    @Override
    public void flush() throws UnitooException {
        barrier.lock();
        try {
            storage.create();
            for (Setting setting : values()) {
                if (setting.isChanged() && !setting.isDynamic()) {
                    storage.putValue(setting.name(), app.format(setting.pop()), null, String.class);
                }
            }
            storage.flush();
            changed = false;
        } finally {
            barrier.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public void reload(boolean first) throws UnitooException {
        barrier.lock();
        try {
            storage.load();

            for (Setting setting : formatterSettings) {
                if (storage.hasValue(setting.name(), null, String.class)) {
                    String s = (String) storage.getValue(setting.name(), null, String.class);
                    if (setting.type() == Character.class) {
                        setting.val(s == null || s.isEmpty() ? null : s.charAt(0));
                    } else {
                        setting.val(s);
                    }
                } else {
                    Object v = setting.pop();
                    storage.putValue(setting.name(), v == null ? "" : v.toString(), null, String.class);
                }
            }

            for (Setting setting : values()) {
                if (storage.hasValue(setting.name(), null, String.class)) {
                    if (!setting.isChanged() || first) {
                        setting.val(app.convert(storage.getValue(setting.name(), null, String.class), setting.type()));
                    }
                } else {
                    storage.putValue(setting.name(), setting.val(), null, String.class);
                }
            }

            storage.flush();
        } finally {
            barrier.unlock();
        }
    }

    @Override
    public void onChanged() {
        try {
            reload(false);
        } catch (UnitooException e) {
            app.log().error("Fail to reload configuration", e);
        }
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    public void startChange() {
        barrier.lock();
    }

    public void endChange() {
        changed = true;
        barrier.unlock();
    }

    @Override
    public void bootComplete() {
        FlushCheckTask task = app.component(FlushCheckTask.class);
        if (task != null) {
            task.startCheck(this);
        }
    }

}
