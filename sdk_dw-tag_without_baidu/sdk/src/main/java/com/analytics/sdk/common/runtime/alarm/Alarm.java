package com.analytics.sdk.common.runtime.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm {

    public static final String TYPE_ALARM_MANAGER = "alarm_mgr";
    public static final String TYPE_JOB_SERVICE = "job_svc";
    public static final String TYPE_HANDLER_THREAD = "handler_thr";

    static final String TAG = Alarm.class.getSimpleName();

    private int alarmDelaySeconds = 30;
    private int alarmId;
    private boolean isLooping;
    private PendingIntent pendingIntent;
    private AlarmHandler alarmHandler;
    private AlarmTask mAlarmTaskImpl;
    private Context mContext;

    public Alarm(Context context) {
        this.mContext = context;
        mAlarmTaskImpl = AlarmTask.sFactoryImpl.create(context,Alarm.TYPE_ALARM_MANAGER);
    }

    public static void cancel(int alarmId) {
        AlarmService.cancel(alarmId);
    }

    public static void cancelAll(){
        AlarmService.cancelAll();
    }

    public boolean isRealy(){
        return (mContext!=null && mAlarmTaskImpl != null);
    }

    public static Alarm make(int alarmId, Context context){
        Alarm alarm = new Alarm(context);
        alarm.alarmId = alarmId;
        return alarm;
    }

    public Alarm setHandler(AlarmHandler handler){
        AlarmHandler alarmHandler = (handler == null ? AlarmHandler.EMPTY : handler);
        this.alarmHandler = new AlarmHandlerProxy(alarmHandler);
        return this;
    }

    public Alarm createPendingIntent(String action,Class<?> broadcastReceiverClass){
        Log.i(TAG,"createPendingIntent action = " + action + " , mContext = " + mContext);
        Intent intent = new Intent();
        intent.setPackage(mContext.getPackageName());
        intent.setAction(action);
        intent.setClass(mContext,broadcastReceiverClass);
        intent.putExtra(AlarmBroadcastHandler.EXTRA_ALARM_ID,this.alarmId);
        this.pendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        return this;
    }

    public PendingIntent getPendingIntent(){
        return this.pendingIntent;
    }

    public Alarm setLooping(boolean looping){
        this.isLooping = looping;
        return this;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public Alarm setType(String type){
        this.mAlarmTaskImpl = AlarmTask.sFactoryImpl.create(mContext,type);
        return this;
    }

    public Alarm setDelaySeconds(int seconds){
        this.alarmDelaySeconds = seconds;
        return this;
    }

    public int getAlarmDelaySeconds(){
        return this.alarmDelaySeconds;
    }

    public AlarmHandler getAlarmHandler(){
        return this.alarmHandler;
    }

    public boolean submit(){
        if(isRealy()){
            LogControl.info(TAG,toString());
            AlarmService.put(getAlarmId(),this);
            return mAlarmTaskImpl.run(this);
        }
        return false;
    }

    public boolean cancel(){
        if(isRealy()){
            return mAlarmTaskImpl.cancel(this);
        }
        return false;
    }

    public int getAlarmId() {
        return this.alarmId;
    }

    public String toString(){
        return "DelaySeconds = " + alarmDelaySeconds + " , alarmId = " + alarmId + " , alarmTask type = " + this.mAlarmTaskImpl.getClass().getSimpleName() + " AlarmHandler = " + this.alarmHandler;
    }

    class AlarmHandlerProxy extends AlarmHandler{

        private AlarmHandler sTarget;

        public AlarmHandlerProxy(AlarmHandler handler){
            this.sTarget = handler;
        }

        @Override
        public boolean handle(Alarm alarm) {
            boolean result = sTarget.handle(alarm);
            return result;
        }
    }

}
