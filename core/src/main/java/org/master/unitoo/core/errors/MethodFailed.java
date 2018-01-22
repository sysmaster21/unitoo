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
public class MethodFailed extends UnitooException {

    public MethodFailed(String method, Throwable t) {
        super(SystemErrorCodes.UTS_MethodFailed, "Method '%1$s' failed to execute", t, method);
    }
}
