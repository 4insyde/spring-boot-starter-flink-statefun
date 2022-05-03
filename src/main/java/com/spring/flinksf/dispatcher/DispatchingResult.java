package com.spring.flinksf.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class DispatchingResult {

    private final Method dispatchedTo;
    private final CompletableFuture<Void> returnValue;

}
