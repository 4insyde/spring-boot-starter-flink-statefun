package com.spring.flinksf.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An {@link Handler} annotation describe method in stateful function that has handler behaviour
 * Method that annotated with {@link Handler} must follow requirements:
 * public modifier,
 * 2 method parameters(where first one is {@link org.apache.flink.statefun.sdk.java.Context} and
 * second one is custom event object).
 *
 * Custom object should have follow requirements:
 * A {@link org.apache.flink.statefun.sdk.java.types.Type<T>} for the custom object must be implemented and
 * added into implementation of {@link com.spring.flinksf.TypeResolver} to be returned by class
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Handler {
}
