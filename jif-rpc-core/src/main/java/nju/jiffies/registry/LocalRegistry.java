package nju.jiffies.registry;

import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {

    private static final ConcurrentHashMap<String, Class<?>> registry = new ConcurrentHashMap<>();

    public static void register(String serviceName, Class<?> implClass) {
        registry.put(serviceName, implClass);
    }

    public static Class<?> get(String serviceName) {
        return registry.get(serviceName);
    }

    public static void remove(String serviceName) {
        registry.remove(serviceName);
    }
}
