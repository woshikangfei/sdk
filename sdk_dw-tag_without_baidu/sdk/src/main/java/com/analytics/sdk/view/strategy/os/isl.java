package com.analytics.sdk.view.strategy.os;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class isl {

    static ActivityMonitor sActivityMonitor;

    public static abstract class ActivityMonitor {

        public static final ActivityMonitor EMPTY = new ActivityMonitor() {
        };

        public void execStartActivity(Intent intent){}
        public void onCallActivityOnCreateMethodBefore(Activity activity){}
        public void onCallActivityOnCreateMethodAfter(Activity activity){}

        public void onCallActivityOnResumeMethodBefore(Activity activity){}
        public void onCallActivityOnResumeMethodAfter(Activity activity){}

        public void onCallActivityOnDestoryMethodBefore(Activity activity){}
        public void onCallActivityOnDestoryMethodAfter(Activity activity){}

    }

    public static void setActivityMonitor(Context context,ActivityMonitor monitor){
        sActivityMonitor = monitor;
    }

    public static ActivityMonitor getActivityMonitor(){
        return (sActivityMonitor == null ? ActivityMonitor.EMPTY : sActivityMonitor);
    }

}
