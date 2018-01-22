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
public class NoSecurityException extends UnitooException {

    public NoSecurityException() {
        super(SystemErrorCodes.UTS_NoSecurityException);
    }

}
