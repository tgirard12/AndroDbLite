package com.androdblite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify a column for deletion. Apply to a Boolean field
 *
 * @author tgirard12
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface DbIsDelete {

    /**
     * Column name
     */
    String value() default "";
}
