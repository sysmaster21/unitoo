/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IRunnableComponent;
import org.master.unitoo.core.errors.ComponentNotActive;
import org.master.unitoo.core.types.CounterStrategy;

/**
 *
 * @author Andrey
 */
public interface ICounter extends IRunnableComponent, IAutowired {

    long next() throws ComponentNotActive;

    CounterStrategy strategy();

    long current();

    long cache();

}
