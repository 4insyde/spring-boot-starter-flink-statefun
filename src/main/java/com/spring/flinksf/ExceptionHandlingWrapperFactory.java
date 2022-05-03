package com.spring.flinksf;

import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
