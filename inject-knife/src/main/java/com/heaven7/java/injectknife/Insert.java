package com.heaven7.java.injectknife;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * insert method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Insert {

    /**
     * the insert method flags
     * @return the insert method flags
     */
    int value() ;

    /**
     * indicate the method from target class.
     * @return the target class.
     */
    Class<?> from() default void.class;
}
