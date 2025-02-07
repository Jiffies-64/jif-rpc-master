package nju.jiffies.feign.controller;

import nju.jiffies.model.User;
import nju.jiffies.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public User getUser() throws Exception {
        User user = userService.getUser("Jiffies");
        System.out.println(user.getName());
        return user;
    }
}
