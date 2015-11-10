package com.androdblite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify a column. Apply to a field
 *
 * @author tgirard12
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface DbColumn {

    /**
     * Column name
     */
    String value() default "";

    /**
     * Field must be insert or update. Primary Key for exemple is FALSE
     */
    boolean insertUpdate() default true;

    /**
     * Field must be retreive in all SELECT queries
     */
    boolean select() default true;
}
