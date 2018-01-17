/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IFormatContext;
import org.master.unitoo.core.types.RequestParamMode;

/**
 *
 * @author Andrey
 */
public class RequestParameter {

    private final String name;
    private final Class type;
    private final RequestParamMode mode;
    private final boolean escaping;
    private final boolean trim;
    private final RequestParameterValidation validation;
    private final String mime;
    private final boolean array;
    private final IFormatContext context;

    public RequestParameter(String name, Class type, RequestParamMode mode,
            boolean escaping, boolean trim, String mime, RequestParameterValidation validation
    ) {
        this.name = name;
        this.mode = mode == null ? RequestParamMode.Param : mode;
        this.escaping = escaping;
        this.trim = trim;
        this.validation = validation;
        this.mime = mime;
        this.array = type.isArray();
        this.type = type.isArray() ? type.getComponentType() : type;
        this.context = new IFormatContext() {
            @Override
            public boolean escape() {
                return RequestParameter.this.escaping;
            }

            @Override
            public boolean trim() {
                return RequestParameter.this.trim;
            }
        };
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

    public boolean escaping() {
        return escaping;
    }

    public boolean trim() {
        return trim;
    }

    public RequestParameterValidation validation() {
        return validation;
    }

    public String mime() {
        return mime;
    }

    public boolean isArray() {
        return array;
    }

    public IFormatContext getFormatContext() {
        return context;
    }

}
