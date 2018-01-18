package com.heaven7.java.injectknife;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * multi insert rules.
 * @author heaven7
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inserts {

    /**
     * indicate multi insert rules.
     * @return multi inserts
     */
    Insert[] value() ;
}
