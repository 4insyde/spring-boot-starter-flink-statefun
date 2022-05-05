package com.spring.flinksf.dispatcher.handler;

import com.spring.flinksf.api.DispatchableFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;

public interface HandlerFacade {

    void indexFunction(DispatchableFunction function);

    Optional<InvokableHandler> getHandler(DispatchableFunction function, Message message);
}
