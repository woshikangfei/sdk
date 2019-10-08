package com.analytics.sdk.view.dispatcher;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.entity.AdResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AdRequestQueue {

    static final String TAG = AdRequestQueue.class.getSimpleName();

    static final AdRequestQueue adRequestQueue = new AdRequestQueue();

    private final List<WeakReference<AdResponse>> adRequestList = new ArrayList<WeakReference<AdResponse>>();

    public static AdRequestQueue getDefault(){
        return adRequestQueue;
    }

    public AdResponse topResponse(){
        if(adRequestList!=null && adRequestList.size()>0){
            WeakReference<AdResponse> ref = adRequestList.get(adRequestList.size()-1);
            if(ref!=null && ref.get()!=null){
                return ref.get();
            }
        }
        return null;
    }

    public AdRequest topRequest(){
        AdResponse adResponse = topResponse();
        if(adResponse != null){
            return adResponse.getClientRequest();
        }
        return null;
    }

    public void push(AdResponse adResponse) {
        adRequestList.add(new WeakReference<AdResponse>(adResponse));
    }

    public void remove(AdResponse adResponse){
        adRequestList.remove(adResponse);
    }

    public int size(){
        return adRequestList.size();
    }

    public void dump(){
        int size = adRequestList.size();
        Logger.i(TAG,"dump adrequest.size = " + size);
    }

}
