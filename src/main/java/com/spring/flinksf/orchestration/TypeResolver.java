package com.spring.flinksf.orchestration;

import org.apache.flink.statefun.sdk.java.types.Type;

public interface TypeResolver {

    <T> Type<T> findByClass(Class<T> clazz);

}
