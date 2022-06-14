package com.spring.flink.statefun;

import org.apache.flink.statefun.sdk.java.types.Type;

/**
 * Indicates a global type resolver that can store types {@link Type} object by target class
 */
public interface TypeResolver {

    void put(Class<?> genericClass, Type<?> type);

    <T> Type<T> findByClass(Class<T> clazz);

}
