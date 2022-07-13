package com.insyde.flink.statefun.dispatcher.handler;

import com.insyde.flink.statefun.api.DispatchableFunction;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class HandlerFacadeImpl implements HandlerFacade {

    private final HandlerMethodAnalyzer analyzer;
    private final HandlerMethodCache cache;

    @Override
    public void indexFunction(DispatchableFunction function) {
        Set<InvokableHandler> handlers = analyzer.analyze(function.getClass());
        cache.put(function.getClass(), handlers);
    }

    @Override
    public Optional<InvokableHandler> getHandler(DispatchableFunction function, Message message) {
        return cache.getHandler(function.getClass(), message);
    }
}
