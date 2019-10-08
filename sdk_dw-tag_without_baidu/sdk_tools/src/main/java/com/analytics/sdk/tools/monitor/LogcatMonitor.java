package com.analytics.sdk.tools.monitor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatMonitor extends AbsMonitor{

    private static final int DELAY = 100;
    private static String TAG = "LogcatMonitor";
    private static String LOG_FINISH = "GTRLogcatCacheFinish：";

    public interface LogCollectorListener {
        void onCollectted(String log);

        LogCollectorListener EMPTY = new LogCollectorListener() {
            @Override
            public void onCollectted(String log) {

            }
        };
    }

    // 缓存区结束的标记:
    private static boolean isCacheFinish = false;
    private static String CACHE_FINISH_FLAG;
    private LogCollectorListener logCollectorListener = LogCollectorListener.EMPTY;

    public void setLogCollectorListener(LogCollectorListener logCollectorListener){
        this.logCollectorListener = (logCollectorListener == null ? LogCollectorListener.EMPTY : logCollectorListener);
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void start(Context context) {
        if (started) {
            return;
        }

        stop();

        if (handler == null) {
            thread = new HandlerThread("GTRLogcatMonitorHandlerThread");
            thread.start();
            handler = new Handler(thread.getLooper());
        }

        isCacheFinish = false;

        // 输出一条log作为本次记录的起始点
        // 此条log之前的所有log内容都属于之前的缓存内容，不予记录
        // CACHE_FINISH_FLAG = LOG_FINISH + System.currentTimeMillis();

        // GTRLog.i(TAG, CACHE_FINISH_FLAG);
        // System.out.println(CACHE_FINISH_FLAG);

        handler.postDelayed(getLogRunnable, DELAY);
        Log.i(TAG, "monitor started");
        started = true;
    }

    @Override
    public void stop() {
        if (!started) {
            return;
        }

        if (handler != null) {
            handler.removeCallbacks(getLogRunnable);
            handler = null;
            thread.quit();
            thread = null;
        }

        Log.i(TAG, "monitor stopped");
        started = false;
    }

    public static String generateLogFinishTag() {
        CACHE_FINISH_FLAG = LOG_FINISH + System.currentTimeMillis();
        return CACHE_FINISH_FLAG;
    }

    public static String getLogFinishTag() {
        return CACHE_FINISH_FLAG;
    }

    private Runnable getLogRunnable = new Runnable() {
        @Override
        public void run() {
            if (threadId == -1) {
                threadId = android.os.Process.myTid();
            }

            Process logcatProcess = null;
            BufferedReader reader = null;

            try {
                String cmd = "logcat  *:V | grep " + android.os.Process.myPid();
//                String cmd = "logcat  *:V | grep ActivityManager";
                logcatProcess = Runtime.getRuntime().exec(cmd);

                reader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!isCacheFinish) {
                        if (CACHE_FINISH_FLAG != null &&
                                line.contains(CACHE_FINISH_FLAG)) {
                            isCacheFinish = true;
                        }
                        continue;
                    }

                    String separator = "\n";

                    String log = new StringBuilder()
                            .append("logcatCollect")
                            .append(separator)
                            .append(line)
                            .append(separator)
                            .append(System.currentTimeMillis())
                            .toString();

                    logCollectorListener.onCollectted(log);

                }
            } catch (SecurityException       |
                    IllegalArgumentException |
                    NullPointerException     |
                    IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (logcatProcess != null) {
                    logcatProcess.destroy();
                }

                if (handler != null) {
                    handler.postDelayed(getLogRunnable, DELAY);
                }
            }
        }
    };

}
