package com.spring.flinksf.orchestration;

import com.spring.flinksf.orchestration.api.DispatchableFunction;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;

public interface FunctionWrapper extends DispatchableFunction, StatefulFunctionSpecFactory {
    StatefulFunction getWrappedFunction();

    @Override
    CompletableFuture<Void> apply(Context context, Message argument);
}
