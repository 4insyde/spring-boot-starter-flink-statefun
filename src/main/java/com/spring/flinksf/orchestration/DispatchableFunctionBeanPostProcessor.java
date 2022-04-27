package com.spring.flinksf.orchestration;

import com.spring.flinksf.orchestration.dispatcher.OrchestrationMessageDispatcher;
import com.spring.flinksf.orchestration.api.DispatchableFunction;
import com.spring.flinksf.orchestration.dispatcher.HandlerMessageDispatcher;
import com.spring.flinksf.orchestration.dispatcher.MessageDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchableFunctionBeanPostProcessor implements BeanPostProcessor {

    private final HandlerMessageDispatcher dispatcher;
    private final DispatchableFunctionWrapperFactory wrapperFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DispatchableFunction) {
            MessageDispatcher orchestrationDispatcher = OrchestrationMessageDispatcher.from(bean, dispatcher);
            return wrapperFactory.create((DispatchableFunction) bean, orchestrationDispatcher);
        } else {
            return bean;
        }
    }
}
