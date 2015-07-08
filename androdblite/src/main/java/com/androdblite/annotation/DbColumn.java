package com.androdblite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tgirard on 26/06/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbColumn {

    String name() default "";

    boolean insertUpdate() default true;

    boolean select() default true;
}
