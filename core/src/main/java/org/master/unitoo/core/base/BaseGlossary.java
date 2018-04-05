/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.Label;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.CustomAttribute;

/**
 *
 * @author Andrey
 * @param <C> - класс кода элемента справочника
 * @param <T> - класс элемента справочника
 */
public abstract class BaseGlossary<C, T extends IGlossaryItem<C>> implements IGlossary<C, T> {

    @Attribute(name = "cached", value = "true")
    private Setting<Boolean> cached;
    private volatile boolean loaded = false;

    private ComponentContext context;
    private final ConcurrentHashMap<C, T> cache = new ConcurrentHashMap<>();
    private final ReentrantLock barrier = new ReentrantLock();

    @Override
    public boolean i18n() {
        return true;
    }

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public ComponentType type() {
        return ComponentType.Glossary;
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
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public Iterable<T> items() {
        try {
            refresh();
        } catch (UnitooException e) {
            app().log().error(e);
        }
        return cache.values();
    }

    @Override
    public boolean cached() {
        return cached.val() == null ? true : cached.val();
    }

    protected abstract void load(GlossaryLoader<C, T> loader) throws UnitooException;

    @Override
    public void reload() throws UnitooException {
        loadNow();
    }

    @Override
    public T etalon() {
        return null;
    }

    private void langRefresh(ILanguage language) {
        try {
            language.refresh();
        } catch (UnitooException e) {
            app().log().error("Language refresh failed: " + language.code(), e);
        }
    }

    protected void loadNow() throws UnitooException {
        try {
            barrier.lock();
            cache.clear();

            load(new GlossaryLoader<C, T>() {
                @Override
                public void add(T item) {
                    cache.put(item.code(), item);
                }

                @Override
                public T get(C code) {
                    return cache.get(code);
                }

            });

            if (i18n()) {
                for (ILanguage language : app().components(ILanguage.class)) {
                    for (T item : cache.values()) {
                        language.register(new Label(app(), "_global.glossary." + name() + "." + item.code(), "" + item.code(), item.defLabel(language.code())));
                    }

                    T etalon = etalon();
                    if (etalon != null) {
                        for (CustomAttribute attr : etalon.attributes()) {
                            language.register(new Label(app(), "_global.glossary." + name() + "._attr." + attr.name(), attr.name(), attr.caption()));
                        }
                    }

                    language.register(new Label(app(), "_global.glossary." + name(), name(), description()));
                    langRefresh(language);
                }
            } else {
                for (ILanguage language : app().components(ILanguage.class)) {
                    language.register(new Label(context.application(), "_global.glossary." + name(), name(), description()));

                    T etalon = etalon();
                    if (etalon != null) {
                        for (CustomAttribute attr : etalon.attributes()) {
                            language.register(new Label(app(), "_global.glossary." + name() + "._attr." + attr.name(), attr.name(), attr.caption()));
                        }
                    }

                    langRefresh(language);
                }
            }
            loaded = true;
        } finally {
            barrier.unlock();
        }
    }

    private void refresh() throws UnitooException {
        try {
            barrier.lock();
            if (!loaded || !cached()) {
                loadNow();
            }
        } finally {
            barrier.unlock();
        }
    }

    protected void startLock() {
        barrier.lock();
    }

    protected void endLock() {
        barrier.unlock();
    }

    @Override
    public T item(C code) {
        if (code == null) {
            return null;
        } else {
            try {
                refresh();
            } catch (UnitooException e) {
                app().log().error(e);
            }
            return cache.get(code);
        }
    }

    @Override
    public String label(C code) {
        return label(code, app().process().language());
    }

    @Override
    public String label(C code, ILanguage language) {
        T item = item(code);
        if (item == null) {
            return "" + code;
        } else {
            if (i18n()) {
                return language.label("_global.glossary." + name() + "." + app().format(code));
            } else {
                return item.defLabel(language.code());
            }
        }
    }

    @Override
    public void destroy() {
    }

    protected interface GlossaryLoader<C, T extends IGlossaryItem<C>> {

        void add(T item);

        T get(C code);
    }
}
