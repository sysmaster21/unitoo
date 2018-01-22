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
public class MethodNotAllowed extends UnitooException {

    public MethodNotAllowed(String method, String mapping) {
        super(SystemErrorCodes.UTS_MethodNotAllowed, "Method '%1$s' is not supported for mapping: '%2s'", method, mapping);
    }

    public MethodNotAllowed(String method, String mapping, Throwable t) {
        super(SystemErrorCodes.UTS_MethodNotAllowed, "Method '%1$s' is not supported for mapping: '%2s'", t, method, mapping);
    }
}
