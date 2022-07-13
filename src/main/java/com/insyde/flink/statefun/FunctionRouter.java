package com.insyde.flink.statefun;

import java.util.concurrent.CompletableFuture;

/**
 * Define a statefun router that receives bytes and invokes target function
 */
public interface FunctionRouter {

    CompletableFuture<byte[]> route(byte[] request);
}
