package nju.jiffies.provider;

import nju.jiffies.registry.LocalRegistry;
import nju.jiffies.server.VertxHttpServer;
import nju.jiffies.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 项目
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
