package com.spring.flinksf.dispatcher.handler;

import com.spring.flinksf.TypeResolver;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class HandlerMethodValidator {

    private final TypeResolver typeResolver;

    public void validateHandler(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 2) {
            Class<?> context = parameterTypes[0];
            Class<?> event = parameterTypes[1];
            validateContextType(context, method);
            validateEventType(event, method);
        } else {
            throw new IncorrectMethodDeclaration("Method " + method.getName() + " annotated with @Handler must have 2 parameters");
        }
    }

    private void validateContextType(Class<?> context, Method method) {
        if (Context.class != context) {
            throw new IncorrectMethodDeclaration("Method " + method.getName() + " annotated with @Handler must have first parameter org.apache.flink.statefun.sdk.java.Context class");
        }
    }

    private void validateEventType(Class<?> event, Method method) {
        Type<?> byClass = typeResolver.findByClass(event);
        if (byClass == null) {
            throw new IncorrectMethodDeclaration("Method " + method.getName() + " annotated with @Handler must have second parameter type that defined in TypeResolver");
        }
    }
}
