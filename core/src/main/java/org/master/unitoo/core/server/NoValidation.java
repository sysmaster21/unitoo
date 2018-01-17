/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseValidator;

/**
 *
 * @author Andrey
 */
@Component("core.validate")
public class NoValidation extends BaseValidator<Object> {

    @Override
    public Object validate(Object value) {
        return value;
    }

}
