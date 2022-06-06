package com.spring.flink.statefun;


import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.dispatcher.MessageDispatcher;

public interface DispatchableFunctionWrapperFactory {

    DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher);

}
