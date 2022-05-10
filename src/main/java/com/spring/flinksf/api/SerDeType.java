package com.spring.flinksf.api;

import org.apache.flink.statefun.sdk.java.types.Type;

public interface SerDeType<T> {

    Type<T> type();
}
