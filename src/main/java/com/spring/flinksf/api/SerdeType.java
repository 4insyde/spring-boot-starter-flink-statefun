package com.spring.flinksf.api;

import org.apache.flink.statefun.sdk.java.types.Type;

public interface SerdeType<T> {

    Class<T> getTypeClass();

    Type<T> getType();
}
