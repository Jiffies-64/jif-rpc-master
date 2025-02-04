package nju.jiffies.provider;

import nju.jiffies.RpcApplication;
import nju.jiffies.bootstrap.ProviderBootstrap;
import nju.jiffies.config.RegistryConfig;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.model.ServiceRegisterInfo;
import nju.jiffies.registry.LocalRegistry;
import nju.jiffies.registry.Registry;
import nju.jiffies.registry.RegistryFactory;
import nju.jiffies.server.VertxHttpServer;
import nju.jiffies.server.tcp.VertxTcpServer;
import nju.jiffies.service.UserService;

import java.util.Collections;
import java.util.stream.Collectors;

public class EasyProviderExample {
    public static void main(String[] args) {
        ServiceRegisterInfo<UserServiceImpl> registerInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        ProviderBootstrap.init(Collections.singletonList(registerInfo));
    }
}
