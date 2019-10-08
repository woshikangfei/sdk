package com.adsdk.demo.hook;

import android.util.Log;

public class GDTLoggerHooker {

    public static final String TAG = GDTLoggerHooker.class.getSimpleName();

    public static String className = "com.qq.e.comm.util.GDTLogger";
    public static String methodName = "d";
    public static String methodSig = "(Ljava/lang/String;)V";

    public static void hook(String msg) {
        Log.i(TAG, "***** GDTLogger.d " + msg);
        backup(msg);
    }

    //原有方法
    public static void backup(String msg) {
        Log.i(TAG, "***** Log.e() should not be here");
    }

}
