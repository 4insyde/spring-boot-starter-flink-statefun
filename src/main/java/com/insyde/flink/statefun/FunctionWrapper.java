package com.insyde.flink.statefun;

import com.insyde.flink.statefun.api.DispatchableFunction;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Interface that indicates stateful function wrapper
 */
public interface FunctionWrapper extends DispatchableFunction, StatefulFunctionSpecFactory {
    StatefulFunction getWrappedFunction();

    @Override
    CompletableFuture<Void> apply(Context context, Message argument);
}
