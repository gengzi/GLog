package fun.gengzi.test;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BootStrapTest {


    public static void main(String[] args) {

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

        threadPoolExecutor.execute(() -> {
            System.out.println("执行");
        });

    }
}
