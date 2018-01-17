/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IComponent;

/**
 *
 * @author Andrey
 * @param <T>
 */
public interface IValidator<T> extends IComponent, IAutowired {

    T validate(T value);

}
