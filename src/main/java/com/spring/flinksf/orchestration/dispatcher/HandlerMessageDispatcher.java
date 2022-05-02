package com.spring.flinksf.orchestration.dispatcher;

import com.spring.flinksf.orchestration.api.DispatchableFunction;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class HandlerMessageDispatcher implements MessageDispatcher {

    private final HandlerMethodCache cache;
    private final HandlerMethodAnalyzer analyzer;

    @Override
    public Optional<DispatchingResult> dispatch(Context context, Message message, Object function) {
        Class<? extends DispatchableFunction> clazz = (Class<? extends DispatchableFunction>) function.getClass();
        Optional<InvokableHandler> handler = cache.getHandler(clazz, message);
        if (handler.isEmpty()) {
            handler = indexAndGet(clazz, message);
        }
        return handler.map(h -> h.invoke(function, context, message));
    }

    private Optional<InvokableHandler> indexAndGet(Class<? extends DispatchableFunction> clazz, Message message) {
        Set<InvokableHandler> handlers = analyzer.analyze(clazz);
        cache.put(clazz, handlers);
        return cache.getHandler(clazz, message);
    }
}