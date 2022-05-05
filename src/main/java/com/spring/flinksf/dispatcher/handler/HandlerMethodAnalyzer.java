package com.spring.flinksf.dispatcher.handler;

import com.spring.flinksf.TypeResolver;
import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.Handler;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class HandlerMethodAnalyzer {

    private final TypeResolver typeResolver;
    private final HandlerMethodValidator validator;

    public Set<InvokableHandler> analyze(Class<? extends DispatchableFunction> functionClass) {
        List<Method> handlers = findHandlers(functionClass);
        handlers.forEach(validator::validateHandler);
        return define(handlers);
    }

    private List<Method> findHandlers(Class<? extends DispatchableFunction> functionClass) {
        return Arrays.stream(functionClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Handler.class))
                .collect(toList());
    }

    private Set<InvokableHandler> define(List<Method> methods) {
        return methods.stream()
                .map(m -> new InvokableHandler(getType(m), m))
                .collect(toSet());
    }

    private Type<?> getType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return typeResolver.findByClass(parameterTypes[1]);
    }
}
