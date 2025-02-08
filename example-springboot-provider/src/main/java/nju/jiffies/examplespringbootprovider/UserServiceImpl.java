package nju.jiffies.examplespringbootprovider;

import nju.jiffies.jifrpc.springboot.starter.annotation.RpcService;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(String username) {
        return new User(username);
    }
}
