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
public class AttributeGetException extends UnitooException {

    public AttributeGetException(String name) {
        super(SystemErrorCodes.UTS_AttributeGetException, "Attribute %1$s failed to get value", name);
    }

    public AttributeGetException(String name, Throwable t) {
        super(SystemErrorCodes.UTS_AttributeGetException, t, "Attribute %1$s failed to get value", name);
    }
}
