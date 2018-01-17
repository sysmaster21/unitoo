/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IFormatContext;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.synthetic.IJsonObject;
import org.master.unitoo.core.base.BaseFormatter;
import org.master.unitoo.core.types.Decision;

/**
 *
 * @author Andrey
 */
public class GsonJsonObjectConvertor implements JsonSerializer<IJsonObject>, JsonDeserializer<IJsonObject> {

    private final BaseFormatter formatter;

    public GsonJsonObjectConvertor(BaseFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public JsonElement serialize(IJsonObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        for (Field field : src.getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers())) {
                field.setAccessible(true);

                String name = field.getName();
                Attribute fann = field.getAnnotation(org.master.unitoo.core.api.annotation.Attribute.class);
                name = fann != null && !fann.name().isEmpty() ? fann.name() : name;

                final boolean escape = UniToo.getEffectiveDecision(formatter.currentFormatContext().escape(), fann == null ? Decision.Parent : fann.escape());
                final boolean trim = UniToo.getEffectiveDecision(formatter.currentFormatContext().trim(), fann == null ? Decision.Parent : fann.trim());
                IFormatContext ctx = new IFormatContext() {
                    @Override
                    public boolean escape() {
                        return escape;
                    }

                    @Override
                    public boolean trim() {
                        return trim;
                    }
                };

                Object value;
                try {
                    value = field.get(src);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    value = null;
                }

                JsonElement element;
                if (value instanceof String) {
                    value = formatter.format(value, ctx);
                    element = context.serialize(value);
                } else if (value instanceof IJsonObject) {
                    try {
                        formatter.enterFormatContext(ctx);
                        element = context.serialize(value);
                    } finally {
                        formatter.exitFormatContext();
                    }
                } else {
                    element = context.serialize(value);
                }

                object.add(name, element);
            }
        }
        return object;
    }

    @Override
    public IJsonObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Class clazz = (Class) typeOfT;
            IJsonObject object = (IJsonObject) clazz.newInstance();
            JsonObject jobj = json.getAsJsonObject();
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers())) {
                    field.setAccessible(true);

                    String name = field.getName();
                    Attribute fann = field.getAnnotation(org.master.unitoo.core.api.annotation.Attribute.class);
                    name = fann != null && !fann.name().isEmpty() ? fann.name() : name;

                    final boolean escape = UniToo.getEffectiveDecision(formatter.currentFormatContext().escape(), fann == null ? Decision.Parent : fann.escape());
                    final boolean trim = UniToo.getEffectiveDecision(formatter.currentFormatContext().trim(), fann == null ? Decision.Parent : fann.trim());
                    IFormatContext ctx = new IFormatContext() {
                        @Override
                        public boolean escape() {
                            return escape;
                        }

                        @Override
                        public boolean trim() {
                            return trim;
                        }
                    };

                    Object value;
                    if (IJsonObject.class.isAssignableFrom(field.getType())) {
                        try {
                            formatter.enterFormatContext(ctx);
                            value = context.deserialize(jobj.get(name), field.getType());
                        } finally {
                            formatter.exitFormatContext();
                        }
                    } else {
                        value = context.deserialize(jobj.get(name), field.getType());
                    }

                    if (value instanceof String) {
                        value = formatter.format(value, ctx);
                    }
                    field.set(object, value);
                }
            }
            return object;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new JsonParseException(e);
        }
    }

}
