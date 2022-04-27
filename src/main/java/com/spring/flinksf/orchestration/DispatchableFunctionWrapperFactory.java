package com.spring.flinksf.orchestration;


import com.spring.flinksf.orchestration.api.DispatchableFunction;
import com.spring.flinksf.orchestration.dispatcher.MessageDispatcher;

public interface DispatchableFunctionWrapperFactory {

    DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher);

}
