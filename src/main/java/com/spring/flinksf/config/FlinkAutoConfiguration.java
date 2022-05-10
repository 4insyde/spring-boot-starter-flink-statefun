package com.spring.flinksf.config;

import com.spring.flinksf.*;
import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.api.FunctionRouteController;
import com.spring.flinksf.dispatcher.HandlerMessageDispatcher;
import com.spring.flinksf.dispatcher.handler.*;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * A {@link FlinkAutoConfiguration} define starter beans and scan {@link FunctionRouteController,DispatchableFunctionBeanPostProcessor}
 * to load all component that is mandatory for starter work
 */
@Configuration
@ComponentScan(basePackageClasses = {FunctionRouteController.class, DispatchableFunctionBeanPostProcessor.class})
public class FlinkAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public HandlerMethodCache handlerMethodCache() {
        return new InmemoryHandlerMethodCache();
    }

    @Bean
    public HandlerMethodAnalyzer handlerMethodAnalyzer(TypeResolver typeResolver, HandlerMethodValidator validator) {
        return new HandlerMethodAnalyzer(typeResolver, validator);
    }

    @Bean
    public HandlerMessageDispatcher messageDispatcher(HandlerFacade facade) {
        return new HandlerMessageDispatcher(facade);
    }

    @Bean
    public HandlerFacade handlerFacade(HandlerMethodCache cache, HandlerMethodAnalyzer analyzer) {
        return new HandlerFacadeImpl(analyzer, cache);
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
    public FunctionRouter functionRouter(RequestReplyHandler requestReplyHandler) {
        return new FunctionRouterImpl(requestReplyHandler);
    }

    @Bean
    public DispatchableFunctionBeanPostProcessor dispatchableFunctionBeanPostProcessor(HandlerMessageDispatcher handlerMessageDispatcher, DispatchableFunctionWrapperFactory factory, HandlerFacade facade) {
        return new DispatchableFunctionBeanPostProcessor(handlerMessageDispatcher, factory, facade);
    }

    @Bean
    public TypeResolverBeanPostProcessor typeResolverBeanPostProcessor(TypeResolver resolver) {
        return new TypeResolverBeanPostProcessor(resolver);
    }

    @Bean
    public HandlerMethodValidator handlerMethodValidator(TypeResolver typeResolver) {
        return new HandlerMethodValidator(typeResolver);
    }

    @Bean
    public TypeResolver typeResolver() {
        return new TypeResolverImpl();
    }
}
