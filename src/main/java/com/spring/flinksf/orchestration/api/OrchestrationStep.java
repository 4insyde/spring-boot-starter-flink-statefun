package com.spring.flinksf.orchestration.api;

import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.message.Message;

public interface OrchestrationStep {
    void execute(Context context);

    boolean supportsIncomingMessage(Message message);

    Step.StepContext handleIncomingMessage(Context context, Message message);
}
