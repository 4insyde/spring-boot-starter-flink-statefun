package com.spring.flinksf.dispatcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
class InvokableHandler {

    private final Type<?> type;
    private final Method handler;

    public boolean supports(Message message) {
        return type != null && message.is(type);
    }
    public TypeName getTypeName(){
        return type.typeName();
    }
    @SneakyThrows
    public DispatchingResult invoke(Object function, Context context, Message message) {
        Object object = message.as(type);
        CompletableFuture<Void> returnValue = (CompletableFuture<Void>) handler.invoke(function, context, object);
        return DispatchingResult.of(handler, returnValue);
    }
}
