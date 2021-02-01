package fun.gengzi.test;

import org.slf4j.MDC;

import java.glog.base.MDCInheritableThreadLocal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BootStrapTest {


    public static void main(String[] args) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();


        final AtomicInteger num = new AtomicInteger(1);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                5 + 1,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10_00), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "test-thread" + num.getAndIncrement());
            }
        });

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("haha","haha");
        MDCInheritableThreadLocal.set(objectObjectHashMap);

        threadPoolExecutor.execute(() -> {
            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
            System.out.println(copyOfContextMap.get("haha"));
            System.out.println("执行");
        });

    }
}
