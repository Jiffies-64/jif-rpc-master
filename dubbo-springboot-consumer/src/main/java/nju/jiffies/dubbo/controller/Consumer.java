package nju.jiffies.dubbo.controller;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class Consumer {

    @DubboReference
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
    public User run(String... args) throws Exception {
        User user = userService.getUser(randomString(10));
        System.out.println(user.getName());
        return user;
    }
}
