/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.errors.SystemException;
import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 */
public class StandartResponse implements IBusinessObject {

    private final static StandartResponse EMPTY_RESPONSE = new StandartResponse("000", null);

    public static StandartResponse Success() {
        return EMPTY_RESPONSE;
    }

    public static StandartResponse Error(Throwable t) {
        if (t instanceof UnitooException) {
            return new StandartResponse(((UnitooException) t).code(), ((UnitooException) t).getMessage());
        } else {
            SystemException error = new SystemException(t);
            return new StandartResponse(error.code(), error.getMessage());
        }
    }

    public static StandartResponse Error(String code, String message) {
        return new StandartResponse(code, message);
    }

    private final String errorCode;
    private final String errorText;

    protected StandartResponse() {
        this("000", null);
    }

    protected StandartResponse(String errorCode, String errorText) {
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

}
