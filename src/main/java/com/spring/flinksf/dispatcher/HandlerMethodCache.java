package com.spring.flinksf.dispatcher;

import com.spring.flinksf.api.DispatchableFunction;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class HandlerMethodCache {

    private final Map<Class<? extends DispatchableFunction>, Set<InvokableHandler>> cache = new HashMap<>();

    public void put(Class<? extends DispatchableFunction> clazz, Set<InvokableHandler> handlers) {
        cache.put(clazz, handlers);
    }

    public Optional<InvokableHandler> getHandler(Class<? extends DispatchableFunction> clazz, Message message) {
        if (cache.containsKey(clazz)) {
            return cache.get(clazz).stream()
                    .filter(i -> i.supports(message))
                    .findFirst();
        }
        return Optional.empty();
    }
}
