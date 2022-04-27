package com.spring.flinksf.orchestration;

import com.spring.flinksf.orchestration.dispatcher.DispatchingResult;
import com.spring.flinksf.orchestration.dispatcher.MessageDispatcher;
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
                .filter(f -> f.getType().isAssignableFrom(TypeName.class) || f.getType().isAssignableFrom(ValueSpec.class))
                .map(this::getValue)
                .collect(Collectors.groupingBy(Object::getClass));
        TypeName functionTypeName = (TypeName) specFieldValues.get(TypeName.class).get(0);
        ValueSpec[] valueSpecs = specFieldValues.getOrDefault(ValueSpec.class, List.of()).stream()
                .map(v -> (ValueSpec) v)
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
