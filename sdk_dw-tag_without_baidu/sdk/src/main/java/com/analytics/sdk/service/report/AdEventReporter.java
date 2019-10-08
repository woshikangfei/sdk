package com.analytics.sdk.service.report;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.runtime.event.PriorityEventListener;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.handler.AdRequestEventListener;

/**
 * 实现上报
 */
public class AdEventReporter extends AdRequestEventListener {

    public AdEventReporter(AdRequest adRequest){
        super(adRequest);
    }

    @Override
    public boolean onHandleAction(String action, AdResponse adResponse, Object arg2) {

        if(arg2 != null){
            if(arg2 instanceof Boolean){ //表示不上报
                boolean enable = (Boolean) arg2;
                if(enable){
                    return false;
                }
            }
        }

        if(arg2 instanceof AdError){
            ReportData.obtain((AdError) arg2,action,adResponse).startReport();
        } else {
            ReportData.obtain(action,adResponse).startReport();
        }
        return true;
    }

    @Override
    public int getPriority() {
        return PriorityEventListener.MAX_PRIORITY;
    }

    @Override
    public boolean recycle() {
        adRequest = null;
        return true;
    }
}
