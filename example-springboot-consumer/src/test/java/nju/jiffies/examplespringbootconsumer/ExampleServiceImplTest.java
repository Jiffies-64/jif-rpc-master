package nju.jiffies.examplespringbootconsumer;

import nju.jiffies.examplespringbootconsumer.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 单元测试
 */
@SpringBootTest
class ExampleServiceImplTest {

    @Resource
    private UserController userController;

    @Test
    void test1() {
        userController.getUser();
    }
}