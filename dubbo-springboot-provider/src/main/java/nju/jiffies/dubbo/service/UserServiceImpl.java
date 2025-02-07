package nju.jiffies.dubbo.service;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String name) {
        return new User(name);
    }
}
