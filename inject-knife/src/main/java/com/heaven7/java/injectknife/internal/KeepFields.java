package com.heaven7.java.injectknife.internal;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * indicate the fields of target class will not be proguard.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface KeepFields {
}
