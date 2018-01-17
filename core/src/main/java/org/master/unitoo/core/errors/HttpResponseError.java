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
public class HttpResponseError extends UnitooException {

    public HttpResponseError(Integer code) {
        super(SystemErrorCodes.HttpResponseError, "HTTP response error '%1$d'", code);
    }
}
