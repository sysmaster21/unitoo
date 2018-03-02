/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.master.unitoo.core.api.components.IFormatter;

/**
 *
 * @author Andrey
 */
public interface IDataContent extends IComponent {
    
    boolean inParamsUsage();

    String getRootName();

    ContentType contentType(String encoding);

    String getAttributeName(IBusinessObject object, IBusinessField field);

    String getItemName(IBusinessObject object, IBusinessField field);

    String getItemKeyName(IBusinessObject object, IBusinessField field);

    String getItemValueName(IBusinessObject object, IBusinessField field);

    boolean asAttribute(IBusinessObject object, IBusinessField field);

    IProcessSnapshot beforeSerialize(IBusinessObject object, IBusinessField field, IFormatter formatter);

    void afterSerialize(IProcessSnapshot snapshot);

    IProcessSnapshot beforeDeserialize(IBusinessObject object, IBusinessField field, IFormatter formatter);

    void afterDeserialize(IProcessSnapshot snapshot);

    void serialize(IBusinessObject object, OutputStream stream, IFormatter formatter) throws IOException;

    <O extends IBusinessObject> O deserialize(Class<O> clazz, InputStream stream, IFormatter formatter) throws IOException;

}
