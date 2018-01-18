/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.SecureLevel;

/**
 *
 * @author Andrey
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {

    String value() default "";

    RequestMethod[] type() default {
        RequestMethod.GET,
        RequestMethod.POST
    };

    Class<? extends IErrorHandler> errors() default IErrorHandler.class;

    Class<? extends IFormatter> format() default IFormatter.class;

    SecureLevel secure() default SecureLevel.None;

    String mime() default "";

    Decision escape() default Decision.Parent;

    Decision trim() default Decision.Parent;
}
