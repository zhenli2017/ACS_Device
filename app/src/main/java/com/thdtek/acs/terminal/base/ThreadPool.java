package com.thdtek.acs.terminal.base;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    private static ThreadPoolExecutor exe;

    public synchronized static ThreadPoolExecutor getThread() {
        if (exe == null || exe.isShutdown()) {
            exe = null;
        }
        if (exe == null) {
            exe = new ThreadPoolExecutor(
                    5,
                    10,
                    10,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(2048*5),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
        return exe;
    }
}
