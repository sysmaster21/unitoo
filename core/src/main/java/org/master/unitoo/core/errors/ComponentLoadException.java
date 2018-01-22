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
public class ComponentLoadException extends UnitooException {

    public ComponentLoadException(String name) {
        super(SystemErrorCodes.UTS_ComponentLoadException, "Fail to load component '%1$s'", name);
    }

    public ComponentLoadException(String name, Throwable t) {
        super(SystemErrorCodes.UTS_ComponentLoadException, "Fail to load component '%1$s'", t, name);
    }
}
