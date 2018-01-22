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
public class ParameterEmptyException extends ParameterInvalidException {

    public ParameterEmptyException(String name) {
        super(SystemErrorCodes.UTS_EmptyParameterException, "Parameter %1$s is mandatory", name);
    }
}
