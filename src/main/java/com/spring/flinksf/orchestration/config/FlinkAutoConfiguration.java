package com.spring.flinksf.orchestration.config;

import com.spring.flinksf.orchestration.*;
import com.spring.flinksf.orchestration.api.DispatchableFunction;
import com.spring.flinksf.orchestration.dispatcher.HandlerMessageDispatcher;
import com.spring.flinksf.orchestration.dispatcher.MessageDispatcher;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@ConditionalOnBean(TypeResolver.class)
public class FlinkAutoConfiguration {

    @Bean
    public MessageDispatcher messageDispatcher(TypeResolver typeResolver) {
        return new HandlerMessageDispatcher(typeResolver);
    }

    @Profile("!test")
    @Bean
    public ExceptionHandlingWrapperFactory exceptionHandlingWrapperFactory() {
        return new ExceptionHandlingWrapperFactory();
    }

    @Profile("test")
    @Bean
    public ExceptionThrowingWrapperFactory exceptionThrowingWrapperFactory() {
        return new ExceptionThrowingWrapperFactory();
    }

    @Bean
    StatefulFunctions autoRegisteredStatefulFunctions(List<DispatchableFunction> dispatchableFunctions) {
        StatefulFunctions statefulFunctions = new StatefulFunctions();
        dispatchableFunctions.stream()
                .map(StatefulFunctionSpecFactory.class::cast)
                .map(StatefulFunctionSpecFactory::createSpec)
                .forEach(statefulFunctions::withStatefulFunction);
        return statefulFunctions;
    }

    @Bean
    RequestReplyHandler requestReplyHandler(StatefulFunctions statefulFunctions) {
        return statefulFunctions.requestReplyHandler();
    }

    @Bean
    public FunctionRouter functionRouter(RequestReplyHandler requestReplyHandler){
        return new FunctionRouterImpl(requestReplyHandler);
    }
}
