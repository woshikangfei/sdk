package com.analytics.sdk.common.runtime.alarm;

import android.util.Log;

public final class LogControl {

    static boolean DEBUGABLE = true;

    public static void setDebugable(boolean value){
        DEBUGABLE = value;
    }

    public static void info(String tag,String msg){
        if(DEBUGABLE){
            Log.i(tag,"[XcmAlarm] "+msg);
        }
    }

    public static void error(String tag,String msg){
        if(DEBUGABLE){
            Log.e(tag,"[XcmAlarm] "+msg);
        }
    }

}
