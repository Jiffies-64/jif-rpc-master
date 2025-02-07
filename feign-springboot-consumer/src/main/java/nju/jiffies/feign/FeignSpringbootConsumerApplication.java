package nju.jiffies.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeignSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignSpringbootConsumerApplication.class, args);
    }

}
