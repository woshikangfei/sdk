package com.analytics.sdk.common.runtime.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AlarmBroadcastHandler extends BroadcastReceiver {

    static final String TAG = AlarmBroadcastHandler.class.getSimpleName();

    public static final String ACTION_ALARM_RECEIVER = "com.devy.action.ALARM_RECEIVER";
    public static final String EXTRA_ALARM_ID = "com.devy.extra.ALARM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        int alarmId = intent.getIntExtra(EXTRA_ALARM_ID,-1);

        handle(context,intent);

        LogControl.info(TAG,"onReceive alarmId = " + alarmId);

        Alarm alarm = AlarmService.get(alarmId);

        if (alarm != null && alarm.isLooping()) {
            alarm.submit();
        } else {
            AlarmService.remove(alarmId);
        }

    }

    public abstract void handle(Context context, Intent intent) ;
}
