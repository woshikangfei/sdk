package com.analytics.sdk.common.runtime.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.analytics.sdk.common.log.Logger;

public class AlarmReceiver extends BroadcastReceiver{

    public static final String ACTION_ALARM_RECEIVER = "com.devy.alarm.RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.i("Alarm","onReceive enter , action = " + intent.getAction());


    }


}
