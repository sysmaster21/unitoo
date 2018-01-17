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
import java.util.Date;
import org.master.unitoo.core.api.components.IFormatter;

/**
 *
 * @author Andrey
 */
public class GsonDateConvertor implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final IFormatter formatter;

    public GsonDateConvertor(IFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public synchronized JsonElement serialize(Date value, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatter.date().format(value));
    }

    @Override
    public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return formatter.date().parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

}
