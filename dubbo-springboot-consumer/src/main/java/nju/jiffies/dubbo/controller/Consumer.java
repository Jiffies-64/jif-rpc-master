package nju.jiffies.dubbo.controller;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Consumer {

    @DubboReference
    private UserService userService;

    @GetMapping("/user")
    public User run(String... args) throws Exception {
        User user = userService.getUser("Jiffies");
        System.out.println(user.getName());
        return user;
    }
}
