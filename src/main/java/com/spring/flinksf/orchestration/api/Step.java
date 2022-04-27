package com.spring.flinksf.orchestration.api;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@Builder
@Slf4j
public class Step<T> implements OrchestrationStep {

    @NonNull
    private final StepInteractor<T> interactor;
    @NonNull
    private final Function<Context, T> commandSupplier;
    @NonNull
    private final Function<Context, String> targetFunctionIdSupplier;
    @NonNull
    private final BiConsumer<StepContext, ErrorReason> handleError;
    private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndInterrupt;
    private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndProceed;

    @Override
    public void execute(Context context) {
        String targetFunctionId = targetFunctionIdSupplier.apply(context);
        T command = commandSupplier.apply(context);
        log.info("Sending command {}", command);
        interactor.execute(context, command, targetFunctionId);
    }

    @Override
    public boolean supportsIncomingMessage(Message message) {
        return interactor.supportsIncomingMessage(message);
    }

    @Override
    public StepContext handleIncomingMessage(Context context, Message message) {
        StepContext stepContext = new StepContext(context, handleError, handleAndInterrupt, handleAndProceed);
        interactor.handle(stepContext, message);
        return stepContext;
    }

    @SuppressWarnings("unused") // used by Lombok
    public static class StepBuilder<T> {

        private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndInterrupt = new HashMap<>();
        private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndProceed = new HashMap<>();

        public StepBuilder<T> handleAndInterrupt(Type<?> type, BiConsumer<StepContext, Message> consumer) {
            handleAndInterrupt.put(type.typeName(), consumer);
            return this;
        }

        public StepBuilder<T> handleAndProceed(Type<?> type, BiConsumer<StepContext, Message> consumer) {
            handleAndProceed.put(type.typeName(), consumer);
            return this;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class ErrorReason {

        private final String code;
        private final String message;
    }

    @RequiredArgsConstructor
    @Slf4j
    public static class StepContext {

        @Getter
        private final Context functionContext;
        private final BiConsumer<StepContext, ErrorReason> onError;
        @Getter
        private boolean proceed = true;
        private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndInterrupt;
        private final Map<TypeName, BiConsumer<StepContext, Message>> handleAndProceed;

        public void handleError(ErrorReason errorReason) {
            log.info("Error detected, handling {}...", errorReason);
            proceed = false;
            onError.accept(this, errorReason);
        }

        public void handle(Type<?> type, Message message) {
            log.info("Received incoming message of type {}...", type.typeName());
            TypeName typeName = type.typeName();
            BiConsumer<StepContext, Message> interruptingHandler = handleAndInterrupt.get(typeName);
            if (interruptingHandler != null) {
                interruptingHandler.accept(this, message);
                log.info("Incoming message of type {} handled successfully, interrupting...", type.typeName());
                proceed = false;
            } else {
                BiConsumer<StepContext, Message> proceedingHandler = handleAndProceed.get(typeName);
                if (proceedingHandler != null) {
                    proceedingHandler.accept(this, message);
                    log.info("Incoming message of type {} handled successfully, proceeding to the next step...", type.typeName());
                    proceed = true;
                }
            }
        }

        public void proceed() {
            proceed = true;
        }
    }
}
