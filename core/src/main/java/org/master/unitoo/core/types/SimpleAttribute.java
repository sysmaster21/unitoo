/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

/**
 *
 * @author Andrey
 * @param <T> класс занчения атрибута
 */
public class SimpleAttribute<T> extends CustomAttribute<T> {

    private T value;

    public SimpleAttribute(String name, String caption, Class<T> type) {
        super(name, caption, type);
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public void value(T value) {
        this.value = value;
    }

}
