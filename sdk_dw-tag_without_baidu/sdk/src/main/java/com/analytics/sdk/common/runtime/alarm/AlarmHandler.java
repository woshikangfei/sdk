package com.analytics.sdk.common.runtime.alarm;

public abstract class AlarmHandler {

    public static final AlarmHandler EMPTY = new AlarmHandler(){
        @Override
        public boolean handle(Alarm alarm) {
            return false;
        }
    };

    public abstract boolean handle(Alarm alarm);
}
