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
public class XMLTranformException extends UnitooException {

    public XMLTranformException(Throwable t) {
        super(SystemErrorCodes.XMLTranformException, "XML transformation failed", t);
    }
}
