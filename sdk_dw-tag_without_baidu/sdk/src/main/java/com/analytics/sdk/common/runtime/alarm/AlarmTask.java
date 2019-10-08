package com.analytics.sdk.common.runtime.alarm;

import android.content.Context;
import android.os.Build;

public interface AlarmTask {

    AlarmTask EMPTY = new AlarmTask() {
        @Override
        public boolean run(Alarm alarm) {
            return false;
        }

        @Override
        public boolean cancel(Alarm alarm) {
            return false;
        }
    };

    interface Factory {
        AlarmTask create(Context context, String type);
    }

    Factory sFactoryImpl = new Factory() {
        @Override
        public AlarmTask create(Context context,String type) {

            if (Alarm.TYPE_ALARM_MANAGER.equals(type)) {
                return new AlarmManagerImpl(context);
            } else if(Alarm.TYPE_JOB_SERVICE.equals(type)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return new JobServiceImpl(context);
                }
            } else if(Alarm.TYPE_HANDLER_THREAD.equals(type)) {

            }

            return AlarmTask.EMPTY;
        }
    };

    boolean run(Alarm alarm);
    boolean cancel(Alarm alarm);
}
