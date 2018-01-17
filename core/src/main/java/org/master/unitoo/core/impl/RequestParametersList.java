/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.annotation.Header;
import org.master.unitoo.core.api.annotation.Param;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.server.NoValidation;
import org.master.unitoo.core.types.RequestParamMode;
import org.master.unitoo.core.api.annotation.Get;
import org.master.unitoo.core.api.annotation.Post;
import org.master.unitoo.core.types.MIME;
import org.master.unitoo.core.api.annotation.Content;
import org.master.unitoo.core.api.annotation.Body;
import org.master.unitoo.core.api.annotation.Cookie;

/**
 *
 * @author Andrey
 */
public class RequestParametersList {

    private final RequestParameter[] params;
    private boolean post = false;
    private String contentType;

    public RequestParametersList(IApplication app, Method method, boolean input, boolean parentEscape, boolean parentTrim) {
        Parameter[] mp = method.getParameters();
        Annotation[][] ann = method.getParameterAnnotations();
        params = new RequestParameter[mp.length];
        for (int i = 0; i < mp.length; i++) {
            RequestParamMode mode = RequestParamMode.Param;
            String name = null;
            boolean escaping = false;
            boolean trim = true;
            boolean required = false;
            double min = 0;
            double max = 0;
            String mask = "";
            String mime = "";
            Class<? extends IValidator> validator = NoValidation.class;
            for (Annotation a : ann[i]) {
                if (Header.class == a.annotationType()) {
                    mode = RequestParamMode.Header;
                    Header t = (Header) a;
                    name = t.value();
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    required = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                } else if (Body.class == a.annotationType() && input) {
                    mode = RequestParamMode.Body;
                    Body t = (Body) a;
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    required = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                } else if (Content.class == a.annotationType() && !input) {
                    post = true;
                    mode = RequestParamMode.Body;
                    Content t = (Content) a;
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    contentType = MIME.ByClass(mp[i].getType(), MIME.HTML);
                } else if (Cookie.class == a.annotationType()) {
                    mode = RequestParamMode.Cookie;
                    Cookie t = (Cookie) a;
                    name = t.value();
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    required = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                } else if (Param.class == a.annotationType() && input) {
                    mode = RequestParamMode.Param;
                    Param t = (Param) a;
                    name = t.value();
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    required = t.mandatory();
                    min = t.min();
                    max = t.max();
                    mask = t.mask();
                    validator = t.validate();
                } else if (Get.class == a.annotationType() && !input) {
                    mode = RequestParamMode.Param;
                    Get t = (Get) a;
                    name = t.value();
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                } else if (Post.class == a.annotationType() && !input) {
                    post = true;
                    mode = RequestParamMode.Post;
                    Post t = (Post) a;
                    name = t.value();
                    escaping = UniToo.getEffectiveDecision(parentEscape, t.escape());
                    trim = UniToo.getEffectiveDecision(parentTrim, t.trim());
                    mime = t.mime().isEmpty() ? MIME.ByClass(mp[i].getType(), "") : t.mime();
                } else {
                    mode = RequestParamMode.Variable;
                    name = mp[i].getName();
                }
            }

            RequestParameterValidation validation = new RequestParameterValidation(app, min, max, mask, validator, required);
            params[i] = new RequestParameter(name, mp[i].getType(), mode, escaping, trim, mime, validation);
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

    public boolean isPost() {
        return post;
    }

    public String contentType() {
        return contentType;
    }

}
