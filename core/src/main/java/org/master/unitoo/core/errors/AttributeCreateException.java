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
public class AttributeCreateException extends UnitooException {

    public AttributeCreateException(String name) {
        super(SystemErrorCodes.AttributeCreateException, "Attribute %1$s failed to create", name);
    }

    public AttributeCreateException(String name, Throwable t) {
        super(SystemErrorCodes.AttributeCreateException, t, "Attribute %1$s failed to create", name);
    }
}
