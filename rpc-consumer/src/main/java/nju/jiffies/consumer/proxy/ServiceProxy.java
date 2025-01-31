package nju.jiffies.consumer.proxy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import nju.jiffies.RpcApplication;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.serializer.Serializer;
import nju.jiffies.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        String serverHost = rpcConfig.getServerHost();
        Integer serverPort = rpcConfig.getServerPort();

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
                            .post(StrUtil.format(
                                    "http://{}:{}",
                                    serverHost,
                                    serverPort
                            ))
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
