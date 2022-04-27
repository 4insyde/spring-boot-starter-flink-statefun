package com.spring.flinksf.orchestration;

import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;

public interface StatefulFunctionSpecFactory {
    @SneakyThrows
    StatefulFunctionSpec createSpec();
}
