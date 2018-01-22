/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.errors;

/**
 *
 * @author Andrey
 */
public class TypeConvertExpection extends UnitooException {

    public TypeConvertExpection(Object value, Class clazz) {
        super(SystemErrorCodes.UTS_TypeConvertExpection, "Can't convert value '%1$s' to '%2$s'", value.toString(), clazz.getName());
    }

    public TypeConvertExpection(Object value, Class clazz, Throwable t) {
        super(SystemErrorCodes.UTS_TypeConvertExpection, "Can't convert value '%1$s' to '%2$s'", t, value.toString(), clazz.getName());
    }
}
