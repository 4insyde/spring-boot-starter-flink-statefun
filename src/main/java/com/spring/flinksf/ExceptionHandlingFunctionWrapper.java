package com.spring.flinksf;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlingFunctionWrapper implements FunctionWrapper {

    private static final String FUNCTION_ID_LOG_MARKER = "function_id";
    private static final String MESSAGE_ID = "message_id";
    private final FunctionWrapper functionWrapper;

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) {
        String messageId = UUID.randomUUID().toString();
        MDC.put(FUNCTION_ID_LOG_MARKER, context.self().id());
        MDC.put(MESSAGE_ID, messageId);
        log.info("Incoming message {} for function {} received", message.valueTypeName(), context.self());
        try {
            return functionWrapper.apply(context, message);
        } catch (Exception e) {
            Address self = context.self();
            TypeName type = self.type();
            log.error("Failed to execute function {}.{}:{}", type.namespace(), type.name(), self.id(), e);
        } finally {
            MDC.remove(FUNCTION_ID_LOG_MARKER);
        }
        log.info("Message handled");
        return context.done();
    }

    @SneakyThrows
    @Override
    public StatefulFunctionSpec createSpec() {
        StatefulFunctionSpec spec = functionWrapper.createSpec();
        return StatefulFunctionSpec.builder(spec.typeName())
                .withSupplier(() -> this)
                .withValueSpecs(spec.knownValues().values().toArray(ValueSpec[]::new))
                .build();
    }

    @Override
    public StatefulFunction getWrappedFunction() {
        return functionWrapper.getWrappedFunction();
    }
}
