package com.analytics.sdk.common.runtime.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

/**
 * 测试机型：
 * lenvo android 4.4
 * redmi note3 android 5.0
 * redmi note4x android 7.0
 * vivo y66     android 6.0
 *
 */
public class AlarmManagerImpl implements AlarmTask{

    static final String TAG = AlarmManagerImpl.class.getSimpleName();

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private Context mContext;

    public AlarmManagerImpl(Context context){
        try {
            this.mContext = context;
            this.mAlarmManager = ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE));
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            LogControl.info(TAG,"getSystemService ALARM_SERVICE  = " + localThrowable);
        }
    }

    @Override
    public boolean run(Alarm alarm) {
        if (this.mAlarmManager == null) {
            LogControl.info(TAG,"run mAlarmManager is null , return");
            return false;
        }

        this.mPendingIntent = alarm.getPendingIntent();

        Log.i("Alarm","run start");

        if (this.mPendingIntent == null) {
            Intent intent = new Intent();
            intent.setPackage(this.mContext.getPackageName());
            intent.setAction(AlarmBroadcastHandler.ACTION_ALARM_RECEIVER);
            intent.putExtra(AlarmBroadcastHandler.EXTRA_ALARM_ID,alarm.getAlarmId());
            this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        }

        int delaySeconds = alarm.getAlarmDelaySeconds();

        Calendar delayTime = Calendar.getInstance();
        delayTime.setTimeInMillis(System.currentTimeMillis());
        delayTime.add(Calendar.SECOND, delaySeconds);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("Alarm","#1");
            this.mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delayTime.getTimeInMillis(), this.mPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i("Alarm","#2");
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(delayTime.getTimeInMillis(), this.mPendingIntent);
            this.mAlarmManager.setAlarmClock(alarmClockInfo, this.mPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("Alarm","#3");
            this.mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, delayTime.getTimeInMillis(), this.mPendingIntent);
        } else {
            Log.i("Alarm","#4");
            this.mAlarmManager.set(AlarmManager.RTC_WAKEUP, delayTime.getTimeInMillis(), this.mPendingIntent);
        }
        Log.i("Alarm","run end");
        return true;
    }

    @Override
    public boolean cancel(Alarm alarm) {

        if (this.mAlarmManager == null) {
            LogControl.info(TAG,"cancel mAlarmManager is null , return");
            return false;
        }

        AlarmService.remove(alarm.getAlarmId());
        if(this.mPendingIntent!=null){
            this.mAlarmManager.cancel(this.mPendingIntent);
        }

        return true;
    }


}
