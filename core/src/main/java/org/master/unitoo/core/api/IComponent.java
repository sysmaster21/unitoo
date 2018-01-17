/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
public interface IComponent {

    void init(ComponentContext context);

    void destroy();

    ComponentType type();

    String name();

    String description();

    String version();

    IApplication app();

    String info();

    IBootInfo boot();

}
