/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import java.lang.reflect.Field;
import org.master.unitoo.core.errors.AttributeGetException;
import org.master.unitoo.core.errors.AttributeSetException;

/**
 *
 * @author Andrey
 */
public class JavaFieldAttribute extends CustomAttribute<Object> {

    private final Object owner;
    private final Field field;

    public JavaFieldAttribute(String name, Field field, Object owner) {
        super(name, (Class) field.getType());
        this.owner = owner;
        this.field = field;
    }

    @Override
    public Object value() throws AttributeGetException {
        try {
            return field.get(owner);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new AttributeGetException(name(), e);
        }
    }

    @Override
    public void value(Object value) throws AttributeSetException {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException | IllegalArgumentException ignore) {
            throw new AttributeSetException(name(), ignore);
        }
    }

}
