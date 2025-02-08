package nju.jiffies.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import nju.jiffies.RpcApplication;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.constant.RpcConstant;
import nju.jiffies.fault.tolerant.TolerantStrategy;
import nju.jiffies.fault.tolerant.TolerantStrategyFactory;
import nju.jiffies.loadBalancer.LoadBalancer;
import nju.jiffies.loadBalancer.LoadBalancerFactory;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.protocol.*;
import nju.jiffies.registry.Registry;
import nju.jiffies.registry.RegistryFactory;
import nju.jiffies.retry.RetryStrategy;
import nju.jiffies.retry.RetryStrategyFactory;
import nju.jiffies.serializer.Serializer;
import nju.jiffies.serializer.SerializerFactory;
import nju.jiffies.server.tcp.VertxTcpClient;
import nju.jiffies.server.tcp.VertxTcpServer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfoForSearch = new ServiceMetaInfo();
            serviceMetaInfoForSearch.setServiceName(serviceName);
            serviceMetaInfoForSearch.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfoForSearch.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // 重试策略
            // rpc 请求
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败", e);
        }
    }
}

