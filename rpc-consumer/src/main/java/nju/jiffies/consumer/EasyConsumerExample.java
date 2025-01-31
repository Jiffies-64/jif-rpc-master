package nju.jiffies.consumer;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;

import static nju.jiffies.consumer.ServiceProxyFactory.getProxy;

public class EasyConsumerExample {

    public static void main(String[] args) {
        UserService userService = getProxy(UserService.class);
        User user = userService.getUser("jiffies");
        if (user != null) {
            System.out.println(user.getName());
        } else {
            System.out.println("user == null");
        }
    }

}
