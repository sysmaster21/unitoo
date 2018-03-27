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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.master.unitoo.core.api.IBusinessField;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.ICodedEnum;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.IDataContent;
import org.master.unitoo.core.errors.TypeConvertExpection;

/**
 *
 * @author Andrey
 */
public class GsonJsonObjectConvertor implements JsonSerializer<IBusinessObject>, JsonDeserializer<IBusinessObject> {

    private final IFormatter formatter;
    private IDataContent content;

    public GsonJsonObjectConvertor(IFormatter formatter) {
        this.formatter = formatter;
    }

    public void content(IDataContent content) {
        this.content = content;
    }

    @Override
    public JsonElement serialize(IBusinessObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        for (IBusinessField field : formatter.app().businessFields(src.getClass())) {
            Object value = field.get(src);
            String name = content.getAttributeName(src, field);
            IProcessSnapshot info = content.beforeSerialize(src, field, formatter);
            try {
                JsonElement element;
                if (value instanceof String
                        || value instanceof ICodedEnum) {
                    value = formatter.format(value);
                    element = context.serialize(value);
                } else {
                    element = context.serialize(value);
                }
                object.add(name, element);
            } finally {
                content.afterSerialize(info);
            }
        }
        return object;
    }

    @Override
    public IBusinessObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Class clazz = (Class) typeOfT;
            IBusinessObject object = (IBusinessObject) clazz.newInstance();
            JsonObject jobj = json.getAsJsonObject();
            for (IBusinessField field : formatter.app().businessFields(object.getClass())) {
                String name = content.getAttributeName(object, field);
                if (jobj.has(name)) {
                    IProcessSnapshot info = content.beforeDeserialize(object, field, formatter);

                    try {
                        Object value;
                        if (Collection.class.isAssignableFrom(field.type())) {
                            Collection collection = field.get(object) == null ? (Collection) field.type().newInstance() : (Collection) field.get(object);
                            if (field.itemType() != null) {
                                for (JsonElement element : jobj.get(field.name()).getAsJsonArray()) {
                                    collection.add(context.deserialize(element, field.itemType()));
                                }
                            }
                            value = collection;
                        } else if (Map.class.isAssignableFrom(field.type())) {
                            Map map = field.get(object) == null ? (Map) field.type().newInstance() : (Map) field.get(object);
                            if (field.itemType() != null && field.keyType() != null) {
                                for (Entry<String, JsonElement> entry : jobj.get(field.name()).getAsJsonObject().entrySet()) {
                                    map.put(
                                            formatter.parse(entry.getKey(), field.keyType()),
                                            context.deserialize(entry.getValue(), field.itemType())
                                    );
                                }
                            }
                            value = map;
                        } else {
                            value = context.deserialize(jobj.get(field.name()), field.type());
                        }

                        if (value instanceof String) {
                            value = formatter.format(value);
                        }
                        field.set(value, object);
                    } finally {
                        content.afterDeserialize(info);
                    }
                }
            }
            return object;
        } catch (IllegalAccessException | InstantiationException | TypeConvertExpection e) {
            throw new JsonParseException(e);
        }
    }

}
