/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.synthetic.DateTime;

/**
 *
 * @author Andrey
 */
public class GsonDateTimeConvertor implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    private final IFormatter formatter;

    public GsonDateTimeConvertor(IFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public synchronized JsonElement serialize(DateTime value, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.datetime().format(value));
    }

    @Override
    public synchronized DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return new DateTime(formatter.datetime().parse(jsonElement.getAsString()).getTime());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

}
