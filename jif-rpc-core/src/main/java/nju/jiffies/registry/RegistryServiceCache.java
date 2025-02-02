package nju.jiffies.registry;

import cn.hutool.core.util.StrUtil;
import nju.jiffies.model.ServiceMetaInfo;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.lang.Console.log;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    Map<String, Map<String, ServiceMetaInfo>> serviceCache = new LinkedHashMap<>();

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    void writeCache(String key, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(
                key,
                newServiceCache.stream().collect(Collectors.toMap(ServiceMetaInfo::getRawServiceAddress, info -> info))
        );
    }

    void deleteCache(String key) {
        log("从本地缓存中移除 {}", key);
        List<String> split = StrUtil.split(key, "/");
        String cacheKey = split.get(2);
        String mapKey = split.get(3);
        Map<String, ServiceMetaInfo> serviceMetaInfoMap = serviceCache.get(cacheKey);
        serviceMetaInfoMap.remove(mapKey);
        if (serviceMetaInfoMap.isEmpty()) {
            serviceCache.remove(cacheKey);
        }
    }

    /**
     * 读缓存
     *
     * @return
     */
    List<ServiceMetaInfo> readCache(String key) {
        Map<String, ServiceMetaInfo> serviceMetaInfoSet = this.serviceCache.get(key);
        if (serviceMetaInfoSet == null || serviceMetaInfoSet.isEmpty()) {
            return null;
        }
        return serviceMetaInfoSet.values().stream().toList();
    }

    /**
     * 清空缓存
     */
    void clearCache(String key) {
        this.serviceCache.remove(key);
    }
}

