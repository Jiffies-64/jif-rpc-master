package nju.jiffies.consumer;

import nju.jiffies.bootstrap.ConsumerBootstrap;
import nju.jiffies.config.RpcConfig;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import nju.jiffies.utils.ConfigUtils;

import static nju.jiffies.proxy.ServiceProxyFactory.getProxy;

public class EasyConsumerExample {

    public static void main(String[] args) throws InterruptedException {
        ConsumerBootstrap.init();

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
