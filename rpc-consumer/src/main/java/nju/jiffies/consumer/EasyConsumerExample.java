package nju.jiffies.consumer;

import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import nju.jiffies.utils.ConfigUtils;

import static nju.jiffies.consumer.proxy.ServiceProxyFactory.getProxy;

public class EasyConsumerExample {

    public static void main(String[] args) {
        // 读取配置
        RpcConfig config = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(config);

        // 获取代理对象
        UserService userService = getProxy(UserService.class);
        User user = userService.getUser("jiffies");
        if (user != null) {
            System.out.println(user.getName());
        } else {
            System.out.println("user == null");
        }
    }

}
