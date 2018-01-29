/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.base.BaseDataContent;

/**
 *
 * @author Andrey
 */
@Component("APPLICATION_JSON")
public class JSON_CONTENT extends BaseDataContent {

    @Override
    public ContentType contentType(String encoding) {
        return ContentType.APPLICATION_JSON.withCharset(encoding);
    }

    @Override
    public void serialize(IBusinessObject object, OutputStream stream, IFormatter formatter) throws IOException {
        formatter.json().serialize(object, stream, this);
    }

    @Override
    public <O extends IBusinessObject> O deserialize(Class<O> clazz, InputStream stream, IFormatter formatter) throws IOException {
        return formatter.json().deserialize(clazz, stream, this);
    }
    
}
