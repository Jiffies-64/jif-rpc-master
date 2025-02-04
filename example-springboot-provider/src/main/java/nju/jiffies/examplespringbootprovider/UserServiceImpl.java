package nju.jiffies.examplespringbootprovider;

import nju.jiffies.jifrpc.springboot.starter.annotation.RpcService;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(String username) {
        System.out.println("用户名：" + username);
        return new User(username);
    }
}
