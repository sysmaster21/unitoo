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
public class MethodNotFound extends UnitooException {

    public MethodNotFound(String method) {
        super(SystemErrorCodes.UTS_MethodNotFound, "Method '%1$s' not found", method);
    }

    public MethodNotFound(String method, Throwable t) {
        super(SystemErrorCodes.UTS_MethodNotFound, "Method '%1$s' not found", t, method);
    }
}
