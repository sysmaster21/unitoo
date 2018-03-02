/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components.contents;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
@Component("APPLICATION_OCTET_STREAM")
public class APPLICATION_OCTET_STREAM extends BaseDataContent {

    @Override
    public boolean inParamsUsage() {
        return true;
    }

    @Override
    public ContentType contentType(String encoding) {
        return ContentType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public void serialize(IBusinessObject object, OutputStream stream, IFormatter formatter) throws IOException {
        new ObjectOutputStream(stream).writeObject(object);
    }

    @Override
    public <O extends IBusinessObject> O deserialize(Class<O> clazz, InputStream stream, IFormatter formatter) throws IOException {
        try {
            return (O) new ObjectInputStream(stream).readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

}
