/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.errors.AttributeGetException;
import org.master.unitoo.core.errors.AttributeSetException;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.types.CustomAttribute;
import org.master.unitoo.core.types.JavaFieldAttribute;

/**
 *
 * @author Andrey
 * @param <T> класс кода элемента
 */
public class GlossaryItem<T> implements IGlossaryItem<T> {

    protected static final String ATTR_DEFAULT = "__default";
    protected static final String ATTR_PARENT = "__parent";

    @Attribute(name = ATTR_DEFAULT)
    private final String defLabel;

    private final T code;
    private final IGlossary<T, ? extends GlossaryItem<T>> parent;
    private final ConcurrentHashMap<String, CustomAttribute> attributes = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<Class, AttrInfoList> ATTR_INDEX = new ConcurrentHashMap<>();
    private final static ReentrantLock ATTR_LOCK = new ReentrantLock();

    public GlossaryItem(T code, IGlossary<T, ? extends GlossaryItem<T>> parent) {
        this(code, "", parent);
    }

    public GlossaryItem(T code, String defLabel, IGlossary<T, ? extends GlossaryItem<T>> parent) {
        this.code = code;
        this.parent = parent;
        this.defLabel = defLabel;
        scan();
    }

    private void scan() {
        try {
            ATTR_LOCK.lock();
            AttrInfoList list = ATTR_INDEX.get(getClass());
            if (list == null) {
                list = new AttrInfoList();
                scan(list, getClass());
                ATTR_INDEX.put(getClass(), list);
            }

            for (AttrInfo info : list) {
                try {
                    CustomAttribute attr = new JavaFieldAttribute(info.name, info.field, this);
                    attr.value(parent.app().parse(info.defValue, attr.type()));
                    attributes.put(info.name, attr);
                } catch (UnitooException e) {
                    parent.app().log().error(new AttributeSetException(info.name, e));
                }
            }

        } finally {
            ATTR_LOCK.unlock();
        }
    }

    private void scan(AttrInfoList list, Class clazz) {
        if (clazz.getSuperclass() != null) {
            scan(list, clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            Attribute attribute = field.getAnnotation(Attribute.class);
            if (attribute != null) {
                try {
                    field.setAccessible(true);
                    list.add(new AttrInfo(
                            field,
                            attribute.name().isEmpty() ? field.getName() : attribute.name(),
                            attribute.value(),
                            true,
                            true));
                } catch (SecurityException e) {
                    parent.app().log().error(new AttributeGetException(attribute.name(), e));
                }
            }
        }
    }

    @Override
    public IGlossary<T, ? extends IGlossaryItem<T>> glossary() {
        return parent;
    }

    @Override
    public T code() {
        return code;
    }

    @Override
    public String defLabel() {
        return defLabel;
    }

    @Override
    public String label() {
        return parent.label(code);
    }

    @Override
    public String label(ILanguage language) {
        return language.label(parent.name() + "." + parent.app().format(code));
    }

    @Override
    public Iterable<CustomAttribute> attributes() {
        return attributes.values();
    }

    @Override
    public CustomAttribute attribute(String name) {
        return attributes.get(name);
    }

    private static class AttrInfoList extends ArrayList<AttrInfo> {

    }

    private static class AttrInfo {

        private final Field field;
        private final String name;
        private final String defValue;
        private final boolean escape;
        private final boolean trim;

        public AttrInfo(Field field, String name, String defValue, boolean escape, boolean trim) {
            this.field = field;
            this.name = name;
            this.defValue = defValue;
            this.escape = escape;
            this.trim = trim;
        }

    }

}
