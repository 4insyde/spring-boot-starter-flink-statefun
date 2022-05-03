package com.spring.flinksf;

import java.util.concurrent.CompletableFuture;

public interface FunctionRouter {

    CompletableFuture<byte[]> route(byte[] request);
}
