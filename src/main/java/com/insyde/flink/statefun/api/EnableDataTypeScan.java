package com.insyde.flink.statefun.api;

import com.insyde.flink.statefun.TypeResolver;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enable data types scan that needs for global type resolver {@link TypeResolver}
 */
@Retention(RUNTIME)
@Target(TYPE)
@Component
public @interface EnableDataTypeScan {

    /**
     * Indicates the package that will be scanned for data types {@link DataType}
     * @return project package path to data types that defined as fields
     */
    String[] basePackageScan();
}
