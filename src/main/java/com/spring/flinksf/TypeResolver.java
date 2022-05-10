package com.spring.flinksf;

import com.spring.flinksf.api.SerDeType;
import org.apache.flink.statefun.sdk.java.types.Type;

public interface TypeResolver {

    void put(SerDeType<?> type);

    <T> Type<T> findByClass(Class<T> clazz);

}
