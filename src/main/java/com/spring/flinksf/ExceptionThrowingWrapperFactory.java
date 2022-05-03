package com.spring.flinksf;

import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionThrowingWrapperFactory implements DispatchableFunctionWrapperFactory {

    @Override
    public DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher) {
        log.info("Enabling exception throwing for function {} invocations...", function.getClass().getSimpleName());
        return new DispatchableFunctionWrapper(function, dispatcher);
    }
}
