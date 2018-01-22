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
public class LogonFailed extends UnitooException {

    public LogonFailed() {
        super(SystemErrorCodes.UTS_LogonFailed);
    }

    public LogonFailed(Throwable t) {
        super(SystemErrorCodes.UTS_LogonFailed, t);
    }
}
