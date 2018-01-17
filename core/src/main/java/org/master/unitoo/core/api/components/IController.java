/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.IStoppableComponent;

/**
 *
 * @author Andrey
 */
public interface IController extends IStoppableComponent {

    String url();

    Iterable<IControllerMethod> methods();

    ILogger log();

}
