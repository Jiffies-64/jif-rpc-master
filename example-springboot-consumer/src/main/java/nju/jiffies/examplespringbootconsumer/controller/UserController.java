package nju.jiffies.examplespringbootconsumer.controller;

import nju.jiffies.jifrpc.springboot.starter.annotation.RpcReference;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class UserController {

    @RpcReference(serializeStrategy="kryo")  // "kryo" "hessian"
    private UserService userService;

    public String randomString(int length) {
        // 定义字符集，包含字母和数字
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            // 生成随机索引
            int index = random.nextInt(characters.length());
            // 根据索引从字符集中取出字符并添加到 StringBuilder 中
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }

    @GetMapping("/user")
    public User getUser() {
        User user = userService.getUser(randomString(10));
        System.out.println(user.getName());
        return user;
    }
}
