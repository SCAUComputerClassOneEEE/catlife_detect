package com.scaudachuang.catlife.commons.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hiluyx
 * @since 2021/8/26 23:08
 **/
public class TaskPool {
    private static final Object lock = new Object();
    private static volatile ThreadPoolExecutor executor;
    private static final int coreTs = 5;
    private static final int maxTs = 10;
    private static final long keepAliveTime = 5;

    public static void execute(Runnable runnable) {
        ThreadPoolExecutor executor = getExecutor();
        executor.execute(runnable);
    }

    private static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            synchronized (lock) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(
                            coreTs, maxTs, keepAliveTime, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(10),
                            (r, executor) -> {

                            });
                }
            }
        }
        return executor;
    }
}
