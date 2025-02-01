package nju.jiffies.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import nju.jiffies.serializer.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.hutool.core.lang.Console.log;

/**
 * SPI 加载器
 */
public class SpiLoader {

    /**
     * 存储已经加载的类：接口名 => (key, 实现类)
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 存储已经实例化的类：类路径 => 对象实例
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        for (Class<?> klass : LOAD_CLASS_LIST) {
            load(klass);
        }
    }

    /**
     * 实例化某类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> klass, String key) {
        Map<String, Class<?>> keyClassMap = loaderMap.get(klass.getName());
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", klass.getName()));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 类型不包括 key=%s 的实现", klass.getName(), key));
        }
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                Constructor<?> constructor = implClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                instanceCache.put(implClassName, constructor.newInstance());
            } catch (InstantiationException | IllegalAccessException
                     | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log("加载类型为 {} 的 SPI", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String dir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader isr = new InputStreamReader(resource.openStream());
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] split = line.split("=");
                        keyClassMap.put(split[0], Class.forName(split[1]));
                    }
                } catch (IOException e) {
                    log("IOException");
                } catch (ClassNotFoundException e) {
                    log("ClassNotFoundException");
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }
}
