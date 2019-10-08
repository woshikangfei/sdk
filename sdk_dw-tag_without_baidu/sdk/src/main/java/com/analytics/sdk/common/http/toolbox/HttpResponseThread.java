package com.analytics.sdk.common.http.toolbox;

import android.os.Handler;
import android.os.HandlerThread;

public final class HttpResponseThread extends HandlerThread {
    private static HttpResponseThread sInstance;
    private static Handler sHandler;


    private HttpResponseThread() {
        super("http.res.thread", android.os.Process.THREAD_PRIORITY_DEFAULT);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new HttpResponseThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static Handler getHandler() {
        synchronized (HttpResponseThread.class) {
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

