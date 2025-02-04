package nju.jiffies.examplespringbootconsumer;

import nju.jiffies.jifrpc.springboot.starter.annotation.RpcReference;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 示例服务实现类
 */
@Service
public class ExampleServiceImpl {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    /**
     * 测试方法
     */
    public void test() {
        User resultUser = userService.getUser("Jiffies");
        System.out.println(resultUser.getName());
    }

}
