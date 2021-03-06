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
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.server.NoValidation;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Header {

    String value();

    //-------------------------------------------------------------------------- Validate input
    boolean mandatory() default false;

    double min() default 0;

    double max() default 0;

    String mask() default "";

    Class<? extends IValidator> validate() default NoValidation.class;

    //-------------------------------------------------------------------------- Format (parameter)
    Class<? extends IFormatter> format() default IFormatter.class;

    Class<? extends IDataContent> content() default IDataContent.class;

    Decision escape() default Decision.Parent;

    Decision trim() default Decision.Parent;

}
