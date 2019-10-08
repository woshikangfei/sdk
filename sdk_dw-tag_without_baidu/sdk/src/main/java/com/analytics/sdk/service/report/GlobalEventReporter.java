package com.analytics.sdk.service.report;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventPriorityGetter;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.common.runtime.event.PriorityEventListener;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.report.entity.ReportData;

public class GlobalEventReporter implements PriorityEventListener {

    static final String TAG = "GlobalEventReporter";

    static GlobalEventReporter sDefault = new GlobalEventReporter();

    private GlobalEventReporter(){}

    public static GlobalEventReporter getDefault(){
        return sDefault;
    }

    public void registerSelf(){
//        EventScheduler.addEventListener(AdEventActions.GLOBAL,this);
    }

    public void unregisterSelf(){
//        EventScheduler.deleteEventListeners(AdEventActions.GLOBAL);
    }

    @Override
    public int getPriority() {
        return PriorityEventListener.MAX_PRIORITY - EventPriorityGetter.countPriority();
    }

    @Override
    public boolean handle(Event event) {
        String eventAction = event.getAction();
        String message = event.getArg1();
        Logger.i(TAG,"handle global eventAction = " + eventAction + " , message = " + message);
        ReportData.obtain(message,eventAction).startReport();
        return true;
    }
}
