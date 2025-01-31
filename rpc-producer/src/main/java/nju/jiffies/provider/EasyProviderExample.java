package nju.jiffies.provider;

import nju.jiffies.RpcApplication;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.registry.LocalRegistry;
import nju.jiffies.server.VertxHttpServer;
import nju.jiffies.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {
        // RPC 框架初始化
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 项目
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
