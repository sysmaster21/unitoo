/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.annotation.Header;
import org.master.unitoo.core.api.annotation.Param;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.server.NoValidation;
import org.master.unitoo.core.types.RequestParamMode;
import org.master.unitoo.core.api.annotation.Body;
import org.master.unitoo.core.api.annotation.Cookie;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public class RequestParametersList {

    private final RequestParameter[] params;

    public RequestParametersList(IApplication app, Method method, boolean parentEscape, boolean parentTrim) {
        Parameter[] mp = method.getParameters();
        Annotation[][] ann = method.getParameterAnnotations();
        params = new RequestParameter[mp.length];
        for (int i = 0; i < mp.length; i++) {
            RequestParamMode mode = RequestParamMode.Param;
            String name = null;
            RequestMethod forMethod = RequestMethod.DEFAULT;
            boolean escaping = false;
            boolean trim = true;
            boolean mandatory = false;
            double min = 0;
            double max = 0;
            String mask = "";
            Class<? extends IValidator> validator = NoValidation.class;
            Class<? extends IDataContent> content = IDataContent.class;
            Class<? extends IFormatter> format = IFormatter.class;
            for (Annotation a : ann[i]) {
                if (Header.class == a.annotationType()) {
                    mode = RequestParamMode.Header;
                    Header t = (Header) a;
                    name = t.value();
                    //---------------------------------------------------------- Validation
                    mandatory = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                    //---------------------------------------------------------- Formats
                    format = t.format();
                    content = t.content();
                    escaping = Decision.Get(parentEscape, t.escape());
                    trim = Decision.Get(parentTrim, t.trim());
                } else if (Body.class == a.annotationType()) {
                    mode = RequestParamMode.Body;
                    Body t = (Body) a;
                    //---------------------------------------------------------- Validation
                    mandatory = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                    //---------------------------------------------------------- Formats
                    format = t.format();
                    content = t.content();
                    escaping = Decision.Get(parentEscape, t.escape());
                    trim = Decision.Get(parentTrim, t.trim());
                } else if (Cookie.class == a.annotationType()) {
                    mode = RequestParamMode.Cookie;
                    Cookie t = (Cookie) a;
                    name = t.value();
                    //---------------------------------------------------------- Validation
                    mandatory = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                    //---------------------------------------------------------- Formats
                    format = t.format();
                    content = t.content();
                    escaping = Decision.Get(parentEscape, t.escape());
                    trim = Decision.Get(parentTrim, t.trim());
                } else if (Param.class == a.annotationType()) {
                    mode = RequestParamMode.Param;
                    Param t = (Param) a;
                    name = t.value();
                    forMethod = t.forMethod();
                    //---------------------------------------------------------- Validation
                    mandatory = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                    //---------------------------------------------------------- Formats
                    format = t.format();
                    content = t.content();
                    escaping = Decision.Get(parentEscape, t.escape());
                    trim = Decision.Get(parentTrim, t.trim());
                } else {
                    mode = RequestParamMode.Variable;
                    name = mp[i].getName();
                }
            }

            RequestParameterValidation validation = new RequestParameterValidation(app, min, max, mask, validator, mandatory);
            RequestParameterFormat formats = new RequestParameterFormat(content, format, escaping, trim, app);
            params[i] = new RequestParameter(name, mp[i].getType(), mode, forMethod, validation, formats);
        }
    }

    public RequestParameter[] params() {
        return params;
    }

    public RequestParameter parameter(int i) {
        return params[i];
    }

    public int size() {
        return params.length;
    }

}
