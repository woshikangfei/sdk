package com.analytics.sdk.common.runtime;

import android.os.Handler;
import android.os.HandlerThread;

public final class BackgroupHandlerThread extends HandlerThread {
    private static BackgroupHandlerThread sInstance;
    private static Handler sHandler;


    private BackgroupHandlerThread() {
        super("bg.tasks", android.os.Process.THREAD_PRIORITY_DEFAULT);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new BackgroupHandlerThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static Handler getHandler() {
        synchronized (BackgroupHandlerThread.class) {
            ensureThreadLocked();
            return sHandler;
        }
    }

    public static void removeRunnable(Runnable runnable){
        getHandler().removeCallbacks(runnable);
    }

    public static void post(Runnable runnable){
        getHandler().post(runnable);
    }

    public static void postDelayed(Runnable runnable,long delay){
        getHandler().postDelayed(runnable,delay);
    }

}

