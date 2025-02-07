package nju.jiffies.demo.jmh;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class JmhDemo {

    // 定义基准测试方法
    @Benchmark
    // 设置输出时间单位为纳秒
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public String testStringConcat() {
        // 进行字符串拼接操作
        return "Hello" + "World";
    }

    public static void main(String[] args) throws RunnerException {
        // 构建测试选项
        Options opt = new OptionsBuilder()
                .include(JmhDemo.class.getSimpleName())
                .forks(1)
                .build();

        // 运行测试
        new Runner(opt).run();
    }
}
