package com.spring.flinksf;

import com.spring.flinksf.api.DispatchableFunction;
import com.spring.flinksf.dispatcher.HandlerMessageDispatcher;
import com.spring.flinksf.dispatcher.handler.HandlerFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
public class DispatchableFunctionBeanPostProcessor implements BeanPostProcessor {

    private final HandlerMessageDispatcher dispatcher;
    private final DispatchableFunctionWrapperFactory wrapperFactory;
    private final HandlerFacade handlerFacade;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DispatchableFunction) {
            handlerFacade.indexFunction((DispatchableFunction) bean);
            return wrapperFactory.create((DispatchableFunction) bean, dispatcher);
        } else {
            return bean;
        }
    }
}
