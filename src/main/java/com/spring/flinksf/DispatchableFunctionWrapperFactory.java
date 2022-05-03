package com.spring.flinksf;


import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.dispatcher.MessageDispatcher;

public interface DispatchableFunctionWrapperFactory {

    DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher);

}
