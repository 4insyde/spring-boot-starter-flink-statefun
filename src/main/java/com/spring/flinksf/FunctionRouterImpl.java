package com.spring.flinksf;

import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.apache.flink.statefun.sdk.java.slice.Slice;
import org.apache.flink.statefun.sdk.java.slice.Slices;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class FunctionRouterImpl implements FunctionRouter {

    private final RequestReplyHandler handler;

    public CompletableFuture<byte[]> route(byte[] request) {
        return handler
                .handle(Slices.wrap(request))
                .thenApply(Slice::toByteArray);
    }
}
