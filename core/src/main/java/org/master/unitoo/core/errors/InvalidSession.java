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
public class InvalidSession extends UnitooException {

    public InvalidSession() {
        super(SystemErrorCodes.InvalidSession);
    }

    public InvalidSession(Throwable t) {
        super(SystemErrorCodes.InvalidSession, t);
    }
}
