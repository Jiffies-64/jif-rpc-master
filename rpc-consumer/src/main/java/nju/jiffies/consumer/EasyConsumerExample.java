package nju.jiffies.consumer;

import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import nju.jiffies.utils.ConfigUtils;

import static nju.jiffies.proxy.ServiceProxyFactory.getProxy;

public class EasyConsumerExample {

    public static void main(String[] args) throws InterruptedException {
        // 读取配置
        RpcConfig config = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(config);

        // 获取代理对象
        UserService userService;
        for (int i = 0; i < 100; i++) {
            userService = getProxy(UserService.class);
            User user = userService.getUser("jiffies");
            if (user != null) {
                System.out.println(user.getName());
            } else {
                System.out.println("user == null");
            }
            Thread.sleep(1000);
        }
    }

}
