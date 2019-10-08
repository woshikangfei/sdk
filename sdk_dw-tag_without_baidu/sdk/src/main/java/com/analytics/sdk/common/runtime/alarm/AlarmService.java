package com.analytics.sdk.common.runtime.alarm;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class AlarmService {

    static final String TAG = AlarmService.class.getSimpleName();

    private static ConcurrentHashMap<Integer,Alarm> sAlarmContainer = new ConcurrentHashMap<>();

    public static void put(int id,Alarm value){
        LogControl.info(TAG,"put alarmId = " + id + " , value = " + value);
        sAlarmContainer.put(id,value);
    }

    public static void cancelAll(){
        for(Iterator<Map.Entry<Integer,Alarm>> iter = sAlarmContainer.entrySet().iterator();iter.hasNext();){
            Map.Entry<Integer,Alarm> me = iter.next();
            Alarm alarm = me.getValue();
            alarm.cancel();
        }
    }

    public static Alarm get(int id){
        return sAlarmContainer.get(id);
    }

    public static void remove(int alarmId) {
        LogControl.info(TAG,"remove alarmId = " + alarmId);
        sAlarmContainer.remove(alarmId);
    }

    public static Alarm handle(int alarmId){

        LogControl.info(TAG,"handle alarmId = " + alarmId);

        if(alarmId!=-1){

            LogControl.info(TAG,"handle get , tid = " + Thread.currentThread().getId());

            Alarm alarm = AlarmService.get(alarmId);

            LogControl.info(TAG,"handle alarm = " + alarm);

            if(alarm!=null){
                alarm.getAlarmHandler().handle(alarm);
                return alarm;
            }

        }

        return null;
    }

    public static void cancel(int alarmId) {
        Alarm alarm = AlarmService.get(alarmId);
        if(alarm!=null){
            alarm.cancel();
        }
    }
}
