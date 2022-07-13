package com.insyde.flink.statefun;

import com.insyde.flink.statefun.api.DispatchableFunction;
import com.insyde.flink.statefun.dispatcher.MessageDispatcher;

/**
 * Stateful Function wrapper factory that creates Dispatchable Function {@link DispatchableFunction}with specific
 * wrapper functionality
 */

public interface DispatchableFunctionWrapperFactory {

    DispatchableFunction create(DispatchableFunction function, MessageDispatcher dispatcher);

}
