/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IApplicationDefaults;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.components.ILabelsPack;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.impl.Label;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.Decision;

/**
 *
 * @author Andrey
 */
public abstract class BaseLabelsPack implements ILabelsPack {

    private ComponentContext context;
    private final ConcurrentHashMap<String, Label> labels = new ConcurrentHashMap<>();

    private void scan(Class clazz) {
        if (clazz.getSuperclass() != null) {
            scan(clazz.getSuperclass());
        }

        IApplicationDefaults defaults = app().defaults();

        for (Field field : clazz.getDeclaredFields()) {
            try {
                if (field.getType() == Label.class) {
                    field.setAccessible(true);
                    String name = field.getName();
                    String def = field.getName();
                    boolean escape = defaults.isEscapeControllerResult();
                    boolean trim = defaults.isTrimControllerResult();

                    Attribute attr = field.getAnnotation(Attribute.class);
                    if (attr != null) {
                        name = attr.name().isEmpty() ? name : attr.name();
                        def = attr.value();
                        escape = attr.escape() == Decision.Make
                                ? true
                                : attr.escape() == Decision.Skip ? false : escape;
                        trim = attr.trim() == Decision.Make
                                ? true
                                : attr.trim() == Decision.Skip ? false : trim;
                    }

                    Label label = new Label(
                            app(),
                            clazz.getName() + "." + name,
                            def,
                            escape,
                            trim);
                    field.set(this, label);
                    labels.put(label.key(), label);

                    for (ILanguage language : app().languages()) {
                        language.register(label);
                    }

                }
            } catch (IllegalAccessException e) {
                app().log().error(e);
            }
        }
    }

    @Override
    public Iterable<Label> labels() {
        return labels.values();
    }

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        scan(getClass());
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
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.info();
    }

    @Override
    public ComponentType type() {
        return ComponentType.Labels;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }
}
