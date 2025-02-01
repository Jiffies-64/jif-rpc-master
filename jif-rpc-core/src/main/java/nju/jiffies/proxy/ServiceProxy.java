package nju.jiffies.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import nju.jiffies.RpcApplication;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.constant.RpcConstant;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.registry.Registry;
import nju.jiffies.registry.RegistryFactory;
import nju.jiffies.serializer.Serializer;
import nju.jiffies.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 从注册中心获取
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo metaInfo = new ServiceMetaInfo();
        metaInfo.setServiceName(method.getDeclaringClass().getName());
        metaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(metaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂时没有服务提供者");
        }
        ServiceMetaInfo serviceMetaInfo = serviceMetaInfos.get(0);

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(new Class[]{String.class})
                .args(args)
                .build();

        try {
            byte[] body = serializer.serialize(rpcRequest);
            try (
                    HttpResponse response = HttpRequest
                            .post(serviceMetaInfo.getServiceAddress())
                            .body(body)
                            .execute()
            ) {
                RpcResponse rpcResponse = serializer.deserialize(response.bodyBytes(), RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
