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
public class StorageAccessException extends UnitooException {

    public StorageAccessException(String name, Throwable t) {
        super(SystemErrorCodes.UTS_StorageAccessException, "Storage '%1$s' access failed", t, name);
    }
}
