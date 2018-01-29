/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Andrey
 */
public interface IObjectConvertor {

    void serialize(IBusinessObject object, OutputStream stream, IDataContent content) throws IOException;

    <T extends IBusinessObject> T deserialize(Class<T> clazz, InputStream stream, IDataContent content) throws IOException;

}
