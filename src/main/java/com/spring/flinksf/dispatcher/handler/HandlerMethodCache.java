package com.spring.flinksf.dispatcher.handler;

import com.spring.flinksf.api.DispatchableFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;
import java.util.Set;

public interface HandlerMethodCache {

    void put(Class<? extends DispatchableFunction> clazz, Set<InvokableHandler> handlers);

    Optional<InvokableHandler> getHandler(Class<? extends DispatchableFunction> clazz, Message message);
}
