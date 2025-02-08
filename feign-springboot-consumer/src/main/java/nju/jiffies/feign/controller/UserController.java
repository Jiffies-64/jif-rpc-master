package nju.jiffies.feign.controller;

import nju.jiffies.feign.client.UserClient;
import nju.jiffies.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class UserController {

    @Autowired
    private UserClient userClient;

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
        User user = userClient.getUser(randomString(10));
        System.out.println(user.getName());
        return user;
    }

}
