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
public class FieldInitException extends UnitooException {

    public FieldInitException(String name, Class clazz, Throwable t) {
        super(SystemErrorCodes.FieldInitException, "Field %1$s initialization failed in class %2$s", t, name, clazz.getName());
    }
}
