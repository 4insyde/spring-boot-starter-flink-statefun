package com.spring.flink.statefun;

import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;

/**
 * Factory that allow to create statefun spec for specific function
 */
public interface StatefulFunctionSpecFactory {
    @SneakyThrows
    StatefulFunctionSpec createSpec();
}
