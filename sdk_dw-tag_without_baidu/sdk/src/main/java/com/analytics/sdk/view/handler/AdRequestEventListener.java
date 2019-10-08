package com.analytics.sdk.view.handler;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.PriorityEventListener;
import com.analytics.sdk.service.ad.entity.AdResponse;

/**
 * 只处理指定AdRequest的事件
 */
public abstract class AdRequestEventListener implements PriorityEventListener , IRecycler{

    static final String TAG = AdRequestEventListener.class.getSimpleName();

    protected AdRequest adRequest;

    public AdRequestEventListener(AdRequest adRequest){
        this.adRequest = adRequest;
    }

    @Override
    public boolean handle(Event event) {

        if(adRequest == null){
            Logger.i(TAG,"AdRequestEventListener recycled");
            return false;
        }

        String action = event.getAction();
        AdResponse arg1 = event.getArg1();
        Object arg2 = event.getArg2();

        String receiveEventRequestId = arg1.getClientRequest().getRequestId();
        String myRequestId = this.adRequest.getRequestId();

        if(receiveEventRequestId.equals(myRequestId)) {
            return handleActionInner(action,arg1,arg2);
        }

        return true;
    }

    boolean handleActionInner(String action, AdResponse adResponse, Object object) {

        AdType adType = adResponse.getClientRequest().getAdType();

        Logger.i(TAG,"handleActionInner enter , action = " + action + " , adType = " + adType + " , requestId = " + adRequest.getRequestId());

        return onHandleAction(action,adResponse,object);
    }

    /**
     *
     * @param action
     * @param adResponse 是事件传递过来的，也就是当前事件对应的请求和响应数据
     * @param object
     * @return
     */
    public abstract boolean onHandleAction(String action, AdResponse adResponse, Object object);

    @Override
    public boolean recycle() {
        adRequest = null;
        return true;
    }
}
