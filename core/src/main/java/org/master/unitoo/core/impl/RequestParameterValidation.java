/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.server.NoValidation;

/**
 *
 * @author Andrey
 */
public class RequestParameterValidation {

    private final double min;
    private final double max;
    private final String mask;
    private final Class<? extends IValidator> validatorClass;
    private IValidator validator;
    private final boolean required;
    private final IApplication app;

    public RequestParameterValidation(IApplication app, double min, double max, String mask, Class<? extends IValidator> validator, boolean required) {
        this.min = min;
        this.max = max;
        this.mask = mask;
        this.validatorClass = validator;
        this.required = required;
        this.app = app;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public String mask() {
        return mask;
    }

    public IValidator validator() {
        if (validator == null) {
            if (NoValidation.class != validatorClass) {
                validator = app.component(validatorClass);
            }
        }
        return validator;
    }

    public boolean required() {
        return required;
    }

}
