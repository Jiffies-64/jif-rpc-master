package nju.jiffies.jifrpc.springboot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import nju.jiffies.jifrpc.springboot.starter.annotation.RpcReference;
import nju.jiffies.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC 服务消费者启动
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> klass = bean.getClass();
        for (Field field : klass.getDeclaredFields()) {
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if (annotation != null) {
                Class<?> interfaceClass = annotation.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("动态代理属性设置失败", e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
