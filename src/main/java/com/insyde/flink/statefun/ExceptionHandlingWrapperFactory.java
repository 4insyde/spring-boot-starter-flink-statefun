package com.insyde.flink.statefun;

import com.insyde.flink.statefun.api.DispatchableFunction;
import com.insyde.flink.statefun.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates exception handler function wrapper on top of Dispatchable Function
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingWrapperFactory implements DispatchableFunctionWrapperFactory {

    @Override
    public DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher) {
        log.info("Enabling exception handling for function {} invocations...", function.getClass().getSimpleName());
        DispatchableFunctionWrapper wrapper = new DispatchableFunctionWrapper(function, dispatcher);
        return new ExceptionHandlingFunctionWrapper(wrapper);
    }
}
