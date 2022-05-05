package com.spring.flinksf.dispatcher;

import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.dispatcher.handler.HandlerFacade;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;

@RequiredArgsConstructor
public class HandlerMessageDispatcher implements MessageDispatcher {

    private final HandlerFacade handlerFacade;

    @Override
    public Optional<DispatchingResult> dispatch(Context context, Message message, Object function) {
        return handlerFacade.getHandler((DispatchableFunction) function, message)
                .map(h -> h.invoke(function, context, message));
    }
}