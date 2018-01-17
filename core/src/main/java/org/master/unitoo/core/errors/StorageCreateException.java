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
public class StorageCreateException extends UnitooException {

    public StorageCreateException(String name, Throwable t) {
        super(SystemErrorCodes.StorageCreateException, "Storage '%1$s' create failed", t, name);
    }
}
