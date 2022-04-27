package com.spring.flinksf.orchestration.dispatcher;

import com.spring.flinksf.orchestration.TypeResolver;
import com.spring.flinksf.orchestration.api.Handler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class HandlerMessageDispatcher implements MessageDispatcher {

    private final TypeResolver typeResolver;

    @Override
    public Optional<DispatchingResult> dispatch(Context context, Message message, Object function) {
        Method[] methods = function.getClass().getMethods();
        Optional<InvokableHandler> handlerOptional = Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(Handler.class))
                .map(m -> define(m, function))
                .filter(d -> d.supports(message))
                .findAny();
        return handlerOptional.map(h -> h.invoke(context, message));
    }

    private InvokableHandler define(Method method, Object function) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type<?> type = typeResolver.findByClass(parameterTypes[1]);
        return new InvokableHandler(type, method, function);
    }

    @RequiredArgsConstructor
    @Getter
    private static class InvokableHandler {

        private final Type<?> type;
        private final Method handler;
        private final Object function;

        private boolean supports(Message message) {
            return type != null && message.is(type);
        }

        @SneakyThrows
        private DispatchingResult invoke(Context context, Message message) {
            Object object = message.as(type);
            CompletableFuture<Void> returnValue = (CompletableFuture<Void>) handler.invoke(function, context, object);
            return DispatchingResult.of(handler, returnValue);
        }
    }
}