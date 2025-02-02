package nju.jiffies.retry;


import nju.jiffies.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 发起重试
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
