/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.ICodedEnum;

/**
 *
 * @author Andrey
 */
public class EnumUtils {

    private static final Map<String, EnumCache> ENUM_ITEMS = new ConcurrentHashMap<>();

    public static <K, T extends ICodedEnum<K>> T Get(K key, Class<T> clazz) {
        EnumCache cache = ENUM_ITEMS.get(clazz.getName());
        if (cache == null) {
            cache = new EnumCache();
            for (ICodedEnum item : clazz.getEnumConstants()) {
                cache.put(((ICodedEnum) item).code(), item);
            }
        }
        return (T) cache.get(key);
    }

    public static Class Type(Class<? extends ICodedEnum> clazz) {
        EnumCache cache = ENUM_ITEMS.get(clazz.getName());
        if (cache == null) {
            cache = new EnumCache();
            for (ICodedEnum item : clazz.getEnumConstants()) {
                cache.put(((ICodedEnum) item).code(), item);
            }
        }
        return cache.type;
    }

    public static <T extends Enum> T Get(String key, Class<T> clazz) {
        Map<Object, Object> cache = ENUM_ITEMS.get(clazz.getName());
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            for (Enum item : clazz.getEnumConstants()) {
                cache.put(item.name(), item);
            }
        }
        return (T) cache.get(key);
    }

    private static class EnumCache extends ConcurrentHashMap<Object, Object> {

        private Class type;

        public EnumCache() {
        }

        @Override
        public Object put(Object key, Object value) {
            if (value instanceof ICodedEnum) {
                type = ((ICodedEnum) value).type();
            } else {
                type = value.getClass();
            }
            return super.put(key, value);
        }

    }
}
