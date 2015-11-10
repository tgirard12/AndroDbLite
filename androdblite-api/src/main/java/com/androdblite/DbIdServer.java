package com.androdblite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify a column id server. Apply to a String field
 *
 * @author tgirard12
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface DbIdServer {

    /**
     * Column name
     */
    String value() default "";
}
