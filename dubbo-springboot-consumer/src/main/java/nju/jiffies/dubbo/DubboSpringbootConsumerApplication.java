package nju.jiffies.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DubboSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboSpringbootConsumerApplication.class, args);
    }

}
