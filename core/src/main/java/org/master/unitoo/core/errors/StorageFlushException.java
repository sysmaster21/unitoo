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
public class StorageFlushException extends UnitooException {

    public StorageFlushException(String name, Throwable t) {
        super(SystemErrorCodes.UTS_StorageFlushException, "Storage '%1$s' saving failed", t, name);
    }
}
