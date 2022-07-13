package com.insyde.flink.statefun.dispatcher;

import com.insyde.flink.statefun.dispatcher.handler.HandlerFacade;
import com.insyde.flink.statefun.api.DispatchableFunction;
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