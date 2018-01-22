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
public class ParameterMaskException extends ParameterInvalidException {

    public ParameterMaskException(String name, String mask) {
        super(SystemErrorCodes.UTS_ParameterMaskException, "Parameter %1$s not match to '%2s'", name, mask);
    }
}
