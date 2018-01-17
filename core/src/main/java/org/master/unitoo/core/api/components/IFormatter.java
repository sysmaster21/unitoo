/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import com.google.gson.Gson;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import javax.xml.transform.Transformer;
import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IFormatContext;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.XMLTranformException;
import org.master.unitoo.core.server.Setting;

/**
 *
 * @author Andrey
 */
public interface IFormatter extends IComponent, IAutowired {

    DateFormat date();

    DateFormat time();

    DateFormat datetime();

    NumberFormat decimal();

    NumberFormat integer();

    Transformer xml() throws XMLTranformException;

    String list();

    String format(Object obj);

    String format(Object obj, IFormatContext context);

    <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection;

    <T> T parse(String value, Class<T> clazz, IFormatContext context) throws TypeConvertExpection;

    Gson gson();

    Charset encoding();
    
    Setting[] settings();

}
