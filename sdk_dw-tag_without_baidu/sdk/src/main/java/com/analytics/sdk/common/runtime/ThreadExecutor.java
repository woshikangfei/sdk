package com.analytics.sdk.common.runtime;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.analytics.sdk.common.log.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadExecutor {

    static final String TAG = ThreadExecutor.class.getSimpleName();

    private static final int MESSAGE_RUN_ON_UITHREAD = 0x1;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AdSdkThread #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory,new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Logger.forcePrint(TAG, "******Task rejected, too many task!");
        }
    });

    static final Executor THREAD_POOL_CACHED_EXECUTOR = Executors.newCachedThreadPool();

    private static Handler sMainThreadHandler;

    public static void init(){
        getMainThreadHandler();
        BackgroupHandlerThread.getHandler();
    }

    public static void runOnFixedThreadPool(Runnable runnable){
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public static void runOnCachedThreadPool(Runnable runnable){
        THREAD_POOL_CACHED_EXECUTOR.execute(runnable);
    }

    public static void runOnAndroidHandlerThread(Runnable runnable){
        BackgroupHandlerThread.post(runnable);
    }

    public static void removeOnAndroidHandlerThread(Runnable runnable){
        BackgroupHandlerThread.removeRunnable(runnable);
    }

    public static void runOnAndroidHandlerThread(Runnable runnable, int delay){
        BackgroupHandlerThread.postDelayed(runnable,delay);
    }

    /**
     * execute a runnable on ui thread, then return immediately. see also {@link #runOnUiThread(Runnable, boolean)}
     * @param runnable the runnable prepared to run
     */
    public static void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, false);
    }

    public static void runOnUiThread(Runnable runnable,long delay) {
        getMainThreadHandler().postDelayed(runnable,delay);
    }

    /**
     * execute a runnable on ui thread
     * @param runnable the runnable prepared to run
     * @param waitUtilDone if set true, the caller thread will wait until the specific runnable finished.
     */
    public static void runOnUiThread(Runnable runnable, boolean waitUtilDone) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
            return;
        }

        CountDownLatch countDownLatch = null;
        if (waitUtilDone) {
            countDownLatch = new CountDownLatch(1);
        }
        Pair<Runnable, CountDownLatch> pair = new Pair<>(runnable, countDownLatch);
        getMainThreadHandler().obtainMessage(MESSAGE_RUN_ON_UITHREAD, pair).sendToTarget();
        if (waitUtilDone) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            }
        }
    }

    private static Handler getMainThreadHandler() {
        synchronized (ThreadExecutor.class) {
            if (sMainThreadHandler == null) {
                sMainThreadHandler = new InternalHandler();
            }
            return sMainThreadHandler;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_RUN_ON_UITHREAD) {
                Pair<Runnable, CountDownLatch> pair = (Pair<Runnable, CountDownLatch>) msg.obj;
                try {
                    Runnable runnable = pair.first;
                    runnable.run();

                } finally {
                    if (pair.second != null) {
                        pair.second.countDown();
                    }
                }
            }
        }
    }

}
