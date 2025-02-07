package nju.jiffies.feign.service;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(String name) {
        return new User(name);
    }
}
