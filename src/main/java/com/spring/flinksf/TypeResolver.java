package com.spring.flinksf;

import org.apache.flink.statefun.sdk.java.types.Type;

public interface TypeResolver {

    void put(Class<?> genericClass, Type<?> type);

    <T> Type<T> findByClass(Class<T> clazz);

}
