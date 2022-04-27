package com.spring.flinksf.orchestration;

import java.util.concurrent.CompletableFuture;

public interface FunctionRouter {

    CompletableFuture<byte[]> route(byte[] request);
}
