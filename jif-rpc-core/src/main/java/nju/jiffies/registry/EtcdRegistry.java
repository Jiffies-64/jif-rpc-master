package nju.jiffies.registry;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import nju.jiffies.config.RegistryConfig;
import nju.jiffies.model.ServiceMetaInfo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cn.hutool.core.lang.Console.log;

public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new LinkedHashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .keepaliveTime(Duration.ofMillis(registryConfig.getKeepAliveTime()))
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void heartBeat() {
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();

        // 创建一个30s的续租
        long id = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey.getBytes());
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo).getBytes());
        kvClient.put(key, value);

        // 将键值对与租约绑定，并设置过期事件
        PutOption po = PutOption.builder().withLeaseId(id).build();
        kvClient.put(key, value, po);

        // 添加到本地已注册集合中
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(
                registryKey,
                StandardCharsets.UTF_8
        ));
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        // 优先从缓存获取服务
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache(serviceKey);
        if (serviceMetaInfos != null) {
            // log("从缓存中读取");
            return serviceMetaInfos;
        }

        // 缓存中没有获取到就从注册中心获取
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        CompletableFuture<GetResponse> future = kvClient.get(
                ByteSequence.from(searchPrefix.getBytes()),
                getOption
        );
        try {
            GetResponse response = future.get();
            serviceMetaInfos = response.getKvs().stream().map(keyValue -> {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                watch(key);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                log("从注册中心读取");
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());

            // 写入缓存
            registryServiceCache.writeCache(serviceKey, serviceMetaInfos);

            return serviceMetaInfos;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("服务列表获取失败", e);
        }
    }

    public void destroy() {
        System.out.println("当前节点下线");
        // 下线节点
        // 遍历本节点所有的 key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void watch(String serviceKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceKey);
        if (newWatch) {
            watchClient.watch(
                    ByteSequence.from(serviceKey.getBytes()),
                    watchResponse -> watchResponse.getEvents().forEach(watchEvent -> {
                        log("接收到 WATCH 事件 {}", watchEvent.getEventType());
                        KeyValue keyValue = watchEvent.getKeyValue();
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        if (Objects.requireNonNull(watchEvent.getEventType()) == WatchEvent.EventType.DELETE) {
                            registryServiceCache.deleteCache(key);
                            watchingKeySet.remove(key);
                        }
                    })
            );
        }
    }

}
