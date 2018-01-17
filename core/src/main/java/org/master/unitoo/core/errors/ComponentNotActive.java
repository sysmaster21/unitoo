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
public class ComponentNotActive extends UnitooException {

    public ComponentNotActive(String name) {
        super(SystemErrorCodes.ComponentNotActive, "Component '%1$s' is not active", name);
    }

    public ComponentNotActive(String name, Throwable t) {
        super(SystemErrorCodes.ComponentNotActive, "Component '%1$s' is not active", t, name);
    }
}
