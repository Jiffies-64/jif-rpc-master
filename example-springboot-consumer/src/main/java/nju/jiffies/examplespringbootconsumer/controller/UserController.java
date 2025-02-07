package nju.jiffies.examplespringbootconsumer.controller;

import nju.jiffies.jifrpc.springboot.starter.annotation.RpcReference;
import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RpcReference
    private UserService userService;

    @GetMapping("/user")
    public User getUser() {
        User user = userService.getUser("Jiffies");
        System.out.println(user.getName());
        return user;
    }
}
