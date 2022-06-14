package com.spring.flink.statefun.api;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates a class or field that will be used to import {@link org.apache.flink.statefun.sdk.java.types.Type} type into
 * global type resolver. Type resolver {@link com.spring.flink.statefun.TypeResolver} use types to serialize and
 * deserialize objects that used in functions handlers {@link Handler}
 */
@Retention(RUNTIME)
@Target({TYPE, FIELD})
@Component
public @interface DataType {
}
