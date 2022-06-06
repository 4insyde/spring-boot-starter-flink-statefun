package com.spring.flink.statefun.dispatcher.handler;

import com.spring.flink.statefun.api.DispatchableFunction;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InmemoryHandlerMethodCache implements HandlerMethodCache{

    private final Map<Class<? extends DispatchableFunction>, Map<TypeName, InvokableHandler>> cache = new HashMap<>();

    public void put(Class<? extends DispatchableFunction> clazz, Set<InvokableHandler> handlers) {
        cache.put(clazz, handlers.stream().collect(Collectors.toMap(InvokableHandler::getTypeName, Function.identity())));
    }

    public Optional<InvokableHandler> getHandler(Class<? extends DispatchableFunction> clazz, Message message) {
        return Optional.ofNullable(cache.computeIfPresent(clazz, (k, v) -> v)).map(v -> v.get(message.valueTypeName()));
    }
}
