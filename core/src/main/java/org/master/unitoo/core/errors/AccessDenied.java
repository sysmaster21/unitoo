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
public class AccessDenied extends UnitooException {

    public AccessDenied(String method) {
        super(SystemErrorCodes.UTS_AccessDenied, "Method '%1$s' access denied", method);
    }

    public AccessDenied(String method, Throwable t) {
        super(SystemErrorCodes.UTS_AccessDenied, "Method '%1$s' access denied", t, method);
    }
}
