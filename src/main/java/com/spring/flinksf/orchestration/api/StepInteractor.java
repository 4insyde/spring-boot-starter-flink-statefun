package com.spring.flinksf.orchestration.api;

import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

public interface StepInteractor<T> {

    void execute(Context context, T command, String targetFunctionId);

    boolean supportsIncomingMessage(Message message);

    void handle(Step.StepContext stepContext, Message message);
}
