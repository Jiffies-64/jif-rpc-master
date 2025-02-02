package nju.jiffies;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.retry.FixedIntervalStrategy;
import nju.jiffies.retry.NoRetryStrategy;
import nju.jiffies.retry.RetryStrategy;
import org.junit.Test;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}

