/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import java.text.DateFormat;
import java.text.NumberFormat;
import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.XMLException;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.types.BinaryFormat;
import org.master.unitoo.core.utils.JSONFormat;
import org.master.unitoo.core.utils.XMLFormat;
import org.master.unitoo.core.api.IDataContent;

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

    BinaryFormat binary();

    XMLFormat xml() throws XMLException;

    String list();

    String format(Object obj);

    <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection;

    JSONFormat json();

    String encoding();

    Setting[] settings();

}
