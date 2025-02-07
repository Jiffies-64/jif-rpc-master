package nju.jiffies.retry;

import com.github.rholder.retry.*;
import nju.jiffies.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.lang.Console.log;

public class FixedIntervalStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
//                .withRetryListener(new RetryListener() {
//                    @Override
//                    public <V> void onRetry(Attempt<V> attempt) {
//                        log("重试次数 {}", attempt.getAttemptNumber());
//                    }
//                })
                .build();
        return retryer.call(callable);
    }
}
