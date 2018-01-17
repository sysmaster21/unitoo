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
public class AttributeSetException extends UnitooException {

    public AttributeSetException(String name) {
        super(SystemErrorCodes.AttributeSetException, "Attribute %1$s failed to set value", name);
    }

    public AttributeSetException(String name, Throwable t) {
        super(SystemErrorCodes.AttributeGetException, t, "Attribute %1$s failed to set value", name);
    }
}
