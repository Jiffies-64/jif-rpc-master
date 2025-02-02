package nju.jiffies.provider;

import nju.jiffies.RpcApplication;
import nju.jiffies.config.RegistryConfig;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.registry.LocalRegistry;
import nju.jiffies.registry.Registry;
import nju.jiffies.registry.RegistryFactory;
import nju.jiffies.server.VertxHttpServer;
import nju.jiffies.server.tcp.VertxTcpServer;
import nju.jiffies.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 向本地注册实现类
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 注册服务
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 项目
        // VertxHttpServer server = new VertxHttpServer();
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
