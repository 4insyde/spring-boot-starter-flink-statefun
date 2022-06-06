package com.spring.flink.statefun;

import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;

public interface StatefulFunctionSpecFactory {
    @SneakyThrows
    StatefulFunctionSpec createSpec();
}
