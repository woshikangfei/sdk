package com.analytics.sdk.common.runtime;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.List;

public class ProcessHelper {

    static final String TAG = ProcessHelper.class.getSimpleName();

    private static String getProcessNameByPid(Context context, int pid) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcessList = manager.getRunningAppProcesses();
            if (appProcessList != null) {
                for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
                    if (pid == appProcessInfo.pid) {
                        return appProcessInfo.processName;
                    }
                }
            }

        } catch (Throwable e) {
            Log.w(TAG, e);
        }

        return null;
    }

    public static boolean isMainProcess(Context context) {
        String processName = getProcessNameByPid(context, Process.myPid());
        if (context.getPackageName().equals(processName)) {
            return true;
        }

        return false;
    }



}
