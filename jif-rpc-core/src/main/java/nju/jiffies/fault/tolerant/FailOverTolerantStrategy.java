package nju.jiffies.fault.tolerant;

import lombok.extern.slf4j.Slf4j;
import nju.jiffies.model.RpcResponse;

import java.util.Map;

/**
 * 故障转移处理异常 - 容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("故障转移处理异常", e);  // todo
        return new RpcResponse();
    }
}


