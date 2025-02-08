package nju.jiffies.config;

import lombok.Data;
import nju.jiffies.fault.tolerant.TolerantStrategyKeys;
import nju.jiffies.loadBalancer.LoadBalancerKeys;
import nju.jiffies.retry.RetryStrategyKeys;
import nju.jiffies.serializer.SerializerKeys;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "jif-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名?
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号?
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /* 以下五个都是作为 SPI 机制的配置项 */
    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;


    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;


}

