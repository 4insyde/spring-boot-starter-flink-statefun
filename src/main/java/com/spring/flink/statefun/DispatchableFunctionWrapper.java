package com.spring.flink.statefun;

import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.dispatcher.DispatchingResult;
import com.spring.flink.statefun.dispatcher.MessageDispatcher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.spring.flink.statefun.TypeNameUtil.typeName;

/**
 * Stateful function wrapper that is responsible for creating function spec based on original function
 */
@RequiredArgsConstructor
public class DispatchableFunctionWrapper implements FunctionWrapper {
    @Getter
    private final StatefulFunction wrappedFunction;
    private final MessageDispatcher dispatcher;

    @Override
    public CompletableFuture<Void> apply(Context context, Message argument) {
        return dispatcher.dispatch(context, argument, wrappedFunction)
                .map(DispatchingResult::getReturnValue)
                .orElseThrow();
    }

    @Override
    @SneakyThrows
    public StatefulFunctionSpec createSpec() {
        Field[] declaredFields = wrappedFunction.getClass().getDeclaredFields();
        Map<? extends Class<?>, List<Object>> specFieldValues = Arrays.stream(declaredFields)
                .filter(f -> f.getType().isAssignableFrom(ValueSpec.class))
                .map(this::getValue)
                .collect(Collectors.groupingBy(Object::getClass));
        TypeName functionTypeName = typeName((Class<? extends DispatchableFunction>) wrappedFunction.getClass());
        ValueSpec[] valueSpecs = specFieldValues.getOrDefault(ValueSpec.class, List.of()).stream()
                .map(ValueSpec.class::cast)
                .toArray(ValueSpec[]::new);
        return StatefulFunctionSpec.builder(functionTypeName)
                .withValueSpecs(valueSpecs)
                .withSupplier(() -> this)
                .build();
    }

    @SneakyThrows
    private Object getValue(Field f) {
        return f.get(wrappedFunction);
    }
}
