/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.RequestParamMode;

/**
 *
 * @author Andrey
 */
public class RequestParameter {

    private final String name;
    private final Class type;
    private final RequestParamMode mode;
    private final RequestMethod forMethod;
    private final RequestParameterValidation validation;
    private final RequestParameterFormat formats;
    private final boolean array;

    public RequestParameter(String name, Class type, RequestParamMode mode, RequestMethod forMethod, RequestParameterValidation validation, RequestParameterFormat formats) {
        this.name = name;
        this.forMethod = forMethod;
        this.validation = validation;
        this.formats = formats;
        this.mode = mode == null ? RequestParamMode.Param : mode;
        this.array = type.isArray();
        this.type = type.isArray() ? type.getComponentType() : type;
    }

    public String name() {
        return name;
    }

    public Class type() {
        return type;
    }

    public RequestParamMode mode() {
        return mode;
    }

    public RequestParameterValidation validation() {
        return validation;
    }

    public RequestParameterFormat formats() {
        return formats;
    }

    public boolean isArray() {
        return array;
    }

    public RequestMethod forMethod() {
        return forMethod;
    }

}
