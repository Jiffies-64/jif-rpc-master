package nju.jiffies.jifrpc.springboot.starter.annotation;

import nju.jiffies.jifrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import nju.jiffies.jifrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import nju.jiffies.jifrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动 RPC 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstrap.class, RpcProviderBootstrap.class, RpcInitBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     */
    boolean value() default true;
}
