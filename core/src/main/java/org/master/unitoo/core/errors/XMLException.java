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
public class XMLException extends UnitooException {

    public XMLException(Throwable t) {
        super(SystemErrorCodes.UTS_XMLException, "XML processing failed", t);
    }
}
