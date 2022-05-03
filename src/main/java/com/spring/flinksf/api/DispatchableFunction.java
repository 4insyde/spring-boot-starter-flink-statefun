package com.spring.flinksf.api;

import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;

public interface DispatchableFunction extends StatefulFunction {
    @Override
    default CompletableFuture<Void> apply(Context context, Message argument) {
        return context.done();
    }
}
