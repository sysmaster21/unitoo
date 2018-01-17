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
public class StorageLoadException extends UnitooException {

    public StorageLoadException(String name, Throwable t) {
        super(SystemErrorCodes.StorageLoadException, "Storage '%1$s' loading failed", t, name);
    }

}
