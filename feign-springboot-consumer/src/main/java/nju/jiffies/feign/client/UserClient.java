package nju.jiffies.feign.client;

import nju.jiffies.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-provider", url = "http://localhost:8542")
public interface UserClient {

    @GetMapping("/user")
    User getUser(@RequestParam("name") String name);
}
