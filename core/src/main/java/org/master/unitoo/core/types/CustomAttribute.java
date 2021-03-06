/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import org.master.unitoo.core.errors.AttributeGetException;
import org.master.unitoo.core.errors.AttributeSetException;

/**
 *
 * @author Andrey
 * @param <T> класс занчения атрибута
 */
public abstract class CustomAttribute<T> {

    private final String name;
    private final String caption;
    private final Class<T> type;

    public CustomAttribute(String name, String caption, Class<T> type) {
        this.name = name;
        this.type = type;
        this.caption = caption;
    }

    public String name() {
        return name;
    }

    public String caption() {
        return caption;
    }

    public Class<T> type() {
        return type;
    }

    public abstract T value() throws AttributeGetException;

    public abstract void value(T value) throws AttributeSetException;

}
