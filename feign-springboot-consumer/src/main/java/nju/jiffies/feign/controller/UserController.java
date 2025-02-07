package nju.jiffies.feign.controller;

import nju.jiffies.feign.client.UserClient;
import nju.jiffies.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserClient userClient;

    @GetMapping("/user")
    public User getUser() {
        User user = userClient.getUser();
        System.out.println(user.getName());
        return user;
    }

}
