package com.spring.flink.statefun.api;

import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link DispatchableFunction} a user-defined function that determine to Spring mechanism
 * to load and registered into Spring context using Spring bean definition approaches
 *
 */
public interface DispatchableFunction extends StatefulFunction {

    /**
     * Method from {@link StatefulFunction} that will be used to determine handler and invoke it if it present
     * @param context the {@link Context} of the current invocation
     * @param argument the input message
     */
    @Override
    default CompletableFuture<Void> apply(Context context, Message argument) {
        return context.done();
    }
}
