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
public class SettingParseException extends UnitooException {

    public SettingParseException(String source, String name, Throwable t) {
        super(SystemErrorCodes.SettingParseException, "Setting '%1$s' parse error (%2$s)", t, name, source);
    }
}
