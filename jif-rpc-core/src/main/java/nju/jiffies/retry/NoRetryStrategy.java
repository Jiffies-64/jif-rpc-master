package nju.jiffies.retry;

import lombok.extern.slf4j.Slf4j;
import nju.jiffies.model.RpcResponse;

import java.util.concurrent.Callable;

import static cn.hutool.core.lang.Console.log;

@Slf4j
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
