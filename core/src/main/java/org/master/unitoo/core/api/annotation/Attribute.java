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
import org.master.unitoo.core.types.Decision;

/**
 *
 * @author Andrey
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {

    /**
     * Current class simple name
     */
    public final static String CLASS_SIMPLE = "#CLASS_SIMPLE#";
    /**
     * Current class full name
     */
    public final static String CLASS_FULL = "#CLASS_FULL#";
    /**
     * Current date and time (for Date, Time and DateTime fields)
     */
    public final static String NOW = "#NOW#";

    String value() default "";

    String name() default "";
    
    String caption() default "";

    Decision escape() default Decision.Parent;

    Decision trim() default Decision.Parent;
}
