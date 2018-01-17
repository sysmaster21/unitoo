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
public class ParameterRangeException extends ParameterInvalidException {

    public ParameterRangeException(String name, double min, double max) {
        super(SystemErrorCodes.ParameterRangeException, "Parameter %1$s not in range %2$d - %3$s", name, min, max);
    }
}
