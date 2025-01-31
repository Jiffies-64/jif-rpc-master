package nju.jiffies.provider;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String name) {
        System.out.println("getUser" + name);
        return new User(name);
    }
}
