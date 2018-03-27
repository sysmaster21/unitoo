/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IChangeListener;
import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.Label;
import org.master.unitoo.core.impl.LabelItem;
import org.master.unitoo.core.server.FlushCheckTask;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
public abstract class BaseLanguage implements ILanguage, IChangeListener {

    private ComponentContext context;
    private final ConcurrentHashMap<String, LabelItem> items = new ConcurrentHashMap<>();
    private IExternalStorage<ILanguage, Object> storage;
    private final ReentrantLock barrier = new ReentrantLock();
    private final AtomicBoolean changed = new AtomicBoolean(false);

    @Override
    public void init(I18nManager manager) throws UnitooException {
        storage = manager.register(this);
        storage.listener(this);
        load(true);
    }

    @Override
    public void update(String key, String value) {
        barrier.lock();
        try {
            LabelItem item = items.get(key);
            if (item != null) {
                item.value(value);
                changed.set(true);
            }
        } finally {
            barrier.unlock();
        }
    }

    @Override
    public String label(String key) {
        LabelItem item = items.get(key);
        return item == null
                ? key
                : item.value() == null ? item.def() : item.value();
    }

    @Override
    public boolean register(Label label) {
        barrier.lock();
        try {
            LabelItem item = new LabelItem(label.key(), null, label.def());
            return items.putIfAbsent(label.key(), item) == null;
        } finally {
            barrier.unlock();
        }
    }

    @Override
    public void refresh() throws UnitooException {
        load(false);
    }

    @Override
    public boolean isChanged() {
        return changed.get();
    }

    @Override
    public void onChanged() {
        try {
            load(false);
        } catch (UnitooException e) {
            app().log().error("Fail to reload language", e);
        }
    }

    protected void load(boolean first) throws UnitooException {
        barrier.lock();
        try {
            storage.load();

            for (LabelItem item : items.values()) {
                if (storage.hasValue(item.key(), null, String.class)) {
                    if (!item.isChanged() || first) {
                        item.load((String) storage.getValue(item.key(), null, String.class));
                    }
                } else {
                    storage.putValue(item.key(), item.def(), null, String.class);
                }
            }
            
            storage.flush();
        } finally {
            barrier.unlock();
        }
    }

    @Override
    public void flush() throws UnitooException {
        barrier.lock();
        try {
            storage.create();
            for (LabelItem item : items.values()) {
                if (item.isChanged()) {
                    storage.putValue(item.key(), item.value(), null, String.class);
                }
            }
            storage.flush();
            changed.set(false);
        } finally {
            barrier.unlock();
        }
    }

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public String extKey() {
        return context.internal();
    }

    @Override
    public String code() {
        return extKey();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public ComponentType type() {
        return ComponentType.Language;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public Map<String, String> items(String regexp) {
        Pattern pattern = regexp == null ? null : Pattern.compile(regexp);
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, LabelItem> entry : items.entrySet()) {
            if (pattern == null || pattern.matcher(entry.getKey()).matches()) {
                String s = entry.getValue().value() == null ? entry.getValue().def() : entry.getValue().value();
                if (s != null) {
                    map.put(entry.getKey(), entry.getValue().value());
                }
            }
        }
        return map;
    }

    @Override
    public void bootComplete() {
        FlushCheckTask task = app().component(FlushCheckTask.class);
        if (task != null) {
            task.startCheck(this);
        }
    }

}
