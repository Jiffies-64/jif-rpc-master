package nju.jiffies.jifrpc.springboot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import nju.jiffies.RpcApplication;
import nju.jiffies.bootstrap.ProviderBootstrap;
import nju.jiffies.config.RegistryConfig;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.jifrpc.springboot.starter.annotation.RpcReference;
import nju.jiffies.jifrpc.springboot.starter.annotation.RpcService;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.model.ServiceRegisterInfo;
import nju.jiffies.registry.LocalRegistry;
import nju.jiffies.registry.Registry;
import nju.jiffies.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Collections;

/**
 * RPC 服务提供者
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> klass = bean.getClass();
        RpcService rpcService = klass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 1.获取基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = klass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 2.注册服务
            // 向本地注册
            LocalRegistry.register(serviceName, klass);

            // 注册全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
