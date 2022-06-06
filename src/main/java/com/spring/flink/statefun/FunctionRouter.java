package com.spring.flink.statefun;

import java.util.concurrent.CompletableFuture;

public interface FunctionRouter {

    CompletableFuture<byte[]> route(byte[] request);
}
