package com.spring.flinksf.orchestration.dispatcher;

import com.spring.flinksf.orchestration.api.OrchestrationStep;
import com.spring.flinksf.orchestration.api.OrderedStep;
import com.spring.flinksf.orchestration.api.Step;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
@Slf4j
public class OrchestrationMessageDispatcher implements MessageDispatcher {

    private final MessageDispatcher dispatcher;
    private final StepIndex index;

    public static MessageDispatcher from(Object function, MessageDispatcher dispatcher) {
        Class<?> clazz = function.getClass();
        Method[] methods = clazz.getMethods();
        List<OrchestrationStep> index = Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(OrderedStep.class))
                .sorted(comparingInt(m -> m.getAnnotation(OrderedStep.class).order()))
                .map(m -> getStep(function, m))
                .collect(toUnmodifiableList());
        return new OrchestrationMessageDispatcher(dispatcher, new StepIndex(index));
    }

    @SneakyThrows
    private static OrchestrationStep getStep(Object function, Method m) {
        String name = m.getName();
        Step<?> step = (Step<?>) m.invoke(function);
        String functionName = function.getClass().getSimpleName();
        return new IndexedStep(step, name, functionName);
    }

    @Override
    public Optional<DispatchingResult> dispatch(Context context, Message message, Object function) {
        log.info("Dispatching message {} to function {}", message.valueTypeName(), function.getClass().getSimpleName());
        Optional<OrchestrationStep> nextStep = dispatcher.dispatch(context, message, function)
                .map(this::getStepAfterInitial)
                .orElseGet(() -> dispatchAndGetNextStep(context, message));
        nextStep.ifPresent(s -> s.execute(context));
        return Optional.of(DispatchingResult.of(null, context.done()));
    }

    private Optional<OrchestrationStep> getStepAfterInitial(DispatchingResult dispatchingResult) {
        if (dispatchingResult.isInitialStepDispatched()) {
            log.info("Initial step dispatched, the next step is going to be executed");
            return index.getStep(0);
        }
        return Optional.empty();
    }

    private Optional<OrchestrationStep> dispatchAndGetNextStep(Context context, Message message) {
        log.info("Looking for step that can handle message of type {}...", message.valueTypeName());
        OrchestrationStep step = index.getStepBySupportingMessage(message).orElseThrow();
        log.info("Step {} found, handling incoming message...", step);
        Step.StepContext stepContext = step.handleIncomingMessage(context, message);
        if (stepContext.isProceed()) {
            return index.getStepAfter(step);
        }
        return Optional.empty();
    }

    @RequiredArgsConstructor
    @Slf4j
    private static class StepIndex {

        private final List<OrchestrationStep> steps;

        public Optional<OrchestrationStep> getStep(int index) {
            if (index >= 0 && index < steps.size()) {
                return Optional.of(steps.get(index));
            }
            return Optional.empty();
        }

        public Optional<OrchestrationStep> getStepAfter(OrchestrationStep step) {
            log.info("Looking for the next step after {}", step);
            int processedStepIndex = steps.indexOf(step);
            int nextStepIndex = processedStepIndex + 1;
            if (nextStepIndex < steps.size()) {
                OrchestrationStep nextStep = steps.get(nextStepIndex);
                log.info("Next step found: {}", nextStep);
                return Optional.of(nextStep);
            }
            log.info("Next step not found, is last step already executed?");
            return Optional.empty();
        }

        public Optional<OrchestrationStep> getStepBySupportingMessage(Message message) {
            return steps.stream()
                    .filter(s -> s.supportsIncomingMessage(message))
                    .findFirst();
        }
    }

    @RequiredArgsConstructor
    @ToString(exclude = "step")
    @Slf4j
    private static class IndexedStep implements OrchestrationStep {

        private static final String STEP_NAME = "stepName";
        private static final String FUNCTION_NAME = "functionName";
        private final Step<?> step;
        private final String name;
        private final String functionName;

        @Override
        public void execute(Context context) {
            populateMdc(() -> {
                log.info("Executing step...");
                step.execute(context);
            });
        }

        @Override
        public boolean supportsIncomingMessage(Message message) {
            return populateMdcAndReturn(() -> step.supportsIncomingMessage(message));
        }

        @Override
        public Step.StepContext handleIncomingMessage(Context context, Message message) {
            return populateMdcAndReturn(() -> {
                log.info("Step is handling incoming message {}", message.valueTypeName());
                return step.handleIncomingMessage(context, message);
            });
        }

        void populateMdc(Runnable runnable) {
            MDC.put(STEP_NAME, name);
            MDC.put(FUNCTION_NAME, functionName);
            runnable.run();
            MDC.remove(STEP_NAME);
            MDC.remove(FUNCTION_NAME);
        }

        @SneakyThrows
        <T> T populateMdcAndReturn(Callable<T> callable) {
            MDC.put(STEP_NAME, name);
            MDC.put(FUNCTION_NAME, functionName);
            T returnValue = callable.call();
            MDC.remove(STEP_NAME);
            MDC.remove(FUNCTION_NAME);
            return returnValue;
        }
    }
}
