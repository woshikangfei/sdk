package com.adsdk.demo.hook;

import android.util.Log;

public class AndroidLogHooker {

    public static String className = "android.util.Log";
    public static String methodName = "e";
    public static String methodSig = "(Ljava/lang/String;Ljava/lang/String;)I";

    public static int hook(String tag, String msg) {
        Log.i("AndroidLogHooker", "***** hook success Log.e(): " + tag + ", " + msg);
        return backup(tag, msg);
    }

    //原有方法
    public static int backup(String tag, String msg) {
        Log.i("AndroidLogHooker", "***** Log.e() should not be here");
        return 1;
    }

}
