package com.spring.flinksf.api;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that an annotated class is 'stateful function'. Such candidates are considered as candidates
 * for auto-detection and auto-registration in global stateful functions specification
 */
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface StatefulFunction {

    /**
     * This value may indicate a function namespace that will be used to build function's TypeName.
     * If value is not defined then by default namespace will be equal to function package name
     */
    String namespace() default "";

    /**
     * This value may indicate a function name that will be used to build function's TypeName.
     * If value is not defined then by default name will be equals to function class simple name
     */
    String name() default "";
}
