package com.adsdk.demo;

import android.util.Log;

public class LogControl {

    static final String MSG_PREFIX = "[SdkDemo]";

    public static void i(String tag, String msg){
        Log.i(tag,MSG_PREFIX + " " + msg);
    }

}
