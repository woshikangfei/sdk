package com.analytics.sdk.view.handler.gdt.feedlist;

import android.view.View;

import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.AdViewExt;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.strategy.click.InformationClickRandomStrategy;
import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.util.UUID;

public class GDTNativeAdViewImpl implements AdViewExt {

    static final String TAG = GDTNativeAdViewImpl.class.getSimpleName();
    private View nativeAdView;
    private AdResponse adResponse;
    private NativeUnifiedADData nativeUnifiedADData;
    private String id;

    public GDTNativeAdViewImpl(View nativeAdView, AdResponse adResponse,NativeUnifiedADData nativeUnifiedADData){
        this.nativeAdView = nativeAdView;
        this.adResponse = adResponse;
        this.nativeUnifiedADData = nativeUnifiedADData;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public View getView() {
        return nativeAdView;
    }

    @Override
    public void render() {
        if(nativeAdView != null){
            new InformationClickRandomStrategy().apply(this,adResponse);
        }
    }

    @Override
    public boolean recycle() {
        Logger.i(TAG,"recycle enter");

        nativeAdView = null;
        adResponse = null;
        if(nativeUnifiedADData!=null){
            nativeUnifiedADData.destroy();
            nativeUnifiedADData = null;
        }
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isRecycle() {
        return nativeAdView == null || nativeUnifiedADData == null;
    }
}
