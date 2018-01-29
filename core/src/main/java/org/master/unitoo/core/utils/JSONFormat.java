/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.IObjectConvertor;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.synthetic.DateTime;
import org.master.unitoo.core.types.Time;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public class JSONFormat implements IObjectConvertor {

    private final Gson gson;
    private final GsonJsonObjectConvertor convertor;
    private final IFormatter format;

    public JSONFormat(IFormatter format) {
        this.format = format;
        this.convertor = new GsonJsonObjectConvertor(format);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new GsonDateConvertor(format))
                .registerTypeAdapter(Time.class, new GsonTimeConvertor(format))
                .registerTypeAdapter(DateTime.class, new GsonDateTimeConvertor(format))
                .registerTypeHierarchyAdapter(IBusinessObject.class, this.convertor)
                .disableHtmlEscaping()
                .create();
    }

    @Override
    public void serialize(IBusinessObject object, OutputStream stream, IDataContent content) throws IOException {
        try {
            convertor.content(content);
            OutputStreamWriter writer = new OutputStreamWriter(stream, format.encoding());
            gson.toJson(object, writer);
            writer.flush();
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T extends IBusinessObject> T deserialize(Class<T> clazz, InputStream stream, IDataContent content) throws IOException {
        try {
            convertor.content(content);
            return gson.fromJson(new InputStreamReader(stream, format.encoding()), clazz);
        } catch (JsonIOException | JsonSyntaxException | UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

}
