package com.spring.flinksf.config;

import com.spring.flinksf.*;
import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.FunctionRouteController;
import com.spring.flinksf.dispatcher.HandlerMessageDispatcher;
import com.spring.flinksf.dispatcher.HandlerMethodAnalyzer;
import com.spring.flinksf.dispatcher.HandlerMethodCache;
import com.spring.flinksf.dispatcher.MessageDispatcher;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = FunctionRouteController.class)
@ConditionalOnBean(TypeResolver.class)
public class FlinkAutoConfiguration {

    @Bean
    public HandlerMethodCache handlerMethodCache(){
        return new HandlerMethodCache();
    }

    @Bean
    public HandlerMethodAnalyzer handlerMethodAnalyzer(TypeResolver typeResolver){
        return new HandlerMethodAnalyzer(typeResolver);
    }

    @Bean
    public MessageDispatcher messageDispatcher(HandlerMethodCache cache, HandlerMethodAnalyzer analyzer) {
        return new HandlerMessageDispatcher(cache, analyzer);
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
