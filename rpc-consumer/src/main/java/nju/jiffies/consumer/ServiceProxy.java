package nju.jiffies.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.serializer.JDKSerializer;
import nju.jiffies.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JDKSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(new Class[]{String.class})
                .args(new Object[]{"jiffies"})
                .build();

        try {
            byte[] body = serializer.serialize(rpcRequest);
            try (HttpResponse response = HttpRequest.post("http://127.0.0.1:8080").body(body).execute()) {
                RpcResponse rpcResponse = serializer.deserialize(response.bodyBytes(), RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
