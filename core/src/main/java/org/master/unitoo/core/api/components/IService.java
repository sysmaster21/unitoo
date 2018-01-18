/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IInterfacedComponent;
import org.master.unitoo.core.api.ILoggableComponent;
import org.master.unitoo.core.api.IStoppableComponent;

/**
 *
 * @author Andrey
 */
public interface IService extends IStoppableComponent, IAutowired, IInterfacedComponent, ILoggableComponent {

}
