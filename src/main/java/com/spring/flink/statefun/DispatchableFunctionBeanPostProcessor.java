package com.spring.flink.statefun;

import com.spring.flink.statefun.api.DispatchableFunction;
import com.spring.flink.statefun.dispatcher.handler.HandlerFacade;
import com.spring.flink.statefun.api.StatefulFunction;
import com.spring.flink.statefun.dispatcher.HandlerMessageDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Bean post processor that is looking for beans which are actually stateful functions (annotated with {@link StatefulFunction}
 * and implements {@link DispatchableFunction}) and register them in global statefun spec
 */
@RequiredArgsConstructor
public class DispatchableFunctionBeanPostProcessor implements BeanPostProcessor {

    private final HandlerMessageDispatcher dispatcher;
    private final DispatchableFunctionWrapperFactory wrapperFactory;
    private final HandlerFacade handlerFacade;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(StatefulFunction.class)
                && bean instanceof DispatchableFunction) {
            handlerFacade.indexFunction((DispatchableFunction) bean);
            return wrapperFactory.create((DispatchableFunction) bean, dispatcher);
        } else {
            return bean;
        }
    }
}
