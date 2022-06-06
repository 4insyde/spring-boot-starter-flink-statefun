package com.spring.flink.statefun.dispatcher;

import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;

public interface MessageDispatcher {
    Optional<DispatchingResult> dispatch(Context context, Message message, Object function);
}
