package com.spring.flink.statefun.config;

import com.spring.flink.statefun.*;
import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.api.SerDeType;
import com.spring.flink.statefun.dispatcher.handler.*;
import com.spring.flink.statefun.api.FunctionRouteController;
import com.spring.flink.statefun.dispatcher.HandlerMessageDispatcher;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * A {@link FlinkAutoConfiguration} define starter beans and scan {@link FunctionRouteController,DispatchableFunctionBeanPostProcessor}
 * to load all component that is mandatory for starter work
 */
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
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
    public TypeResolverBeanPostProcessor typeResolverBeanPostProcessor(TypeResolver typeResolver, ConfigProperties configProperties){
        return new TypeResolverBeanPostProcessor(typeResolver, configProperties);
    }

    @Bean
    @DependsOn("typeResolverBeanPostProcessor")
    public HandlerMethodValidator handlerMethodValidator(TypeResolver typeResolver) {
        return new HandlerMethodValidator(typeResolver);
    }

    @Bean
    public TypeResolver typeResolver(List<SerDeType<?>> serDeTypes) {
        return new TypeResolverImpl(serDeTypes);
    }
}