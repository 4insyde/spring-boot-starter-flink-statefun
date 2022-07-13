package com.insyde.flink.statefun.dispatcher.handler;

import com.insyde.flink.statefun.api.DispatchableFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.Optional;

/**
 * A {@link HandlerFacade} interface works as simple facade to hide multiple communications and present one method
 * for each action
 */
public interface HandlerFacade {

    /**
     * A {@code indexFunction} receive {@link DispatchableFunction} function object and index it, to find methods that
     * annotated Handler and satisfy requirements
     * @param function object that will be scanned to find Handlers
     */
    void indexFunction(DispatchableFunction function);

    /**
     * Method return {@code Optional<InvokableHandler>} that should be found in the
     * @param function
     * @param message
     * @return
     */
    Optional<InvokableHandler> getHandler(DispatchableFunction function, Message message);
}
