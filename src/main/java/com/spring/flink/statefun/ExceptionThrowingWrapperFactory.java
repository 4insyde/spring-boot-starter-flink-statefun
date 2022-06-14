package com.spring.flink.statefun;

import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates exception throwing function wrapper on top of Dispatchable Function
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionThrowingWrapperFactory implements DispatchableFunctionWrapperFactory {

    @Override
    public DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher) {
        log.info("Enabling exception throwing for function {} invocations...", function.getClass().getSimpleName());
        return new DispatchableFunctionWrapper(function, dispatcher);
    }
}
