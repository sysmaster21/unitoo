/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.errors;

import org.master.unitoo.core.api.IErrorType;

/**
 *
 * @author Andrey
 */
public class ParameterInvalidException extends UnitooException {

    public ParameterInvalidException(String name) {
        super(SystemErrorCodes.InvalidParameterException, "Parameter %1$s is invalid", name);
    }

    protected ParameterInvalidException(IErrorType code, String message, Throwable t, Object... params) {
        super(code, message, t, params);
    }

    protected ParameterInvalidException(IErrorType code, String message, Object... params) {
        super(code, message, params);
    }
}
