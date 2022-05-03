package com.spring.flinksf;

import org.apache.flink.statefun.sdk.java.types.Type;

public interface TypeResolver {

    <T> Type<T> findByClass(Class<T> clazz);

}
