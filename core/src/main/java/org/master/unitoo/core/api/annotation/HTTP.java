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
import org.master.unitoo.core.api.IDataContent;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.types.RequestMethod;

/**
 * F
 *
 * @author Andrey
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTP {

    String url();

    RequestMethod method() default RequestMethod.DEFAULT;

    Class<? extends IDataContent> request() default IDataContent.class;

    Decision strictMime() default Decision.Parent;

    Default params() default @Default;

    Default response() default @Default;

    Class<? extends IErrorHandler> errors() default IErrorHandler.class;
}
