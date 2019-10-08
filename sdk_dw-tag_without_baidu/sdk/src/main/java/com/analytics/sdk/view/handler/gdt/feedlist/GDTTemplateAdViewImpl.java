package com.analytics.sdk.view.handler.gdt.feedlist;

import android.view.View;

import com.analytics.sdk.client.feedlist.AdViewExt;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.strategy.click.InformationClickRandomStrategy;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.UUID;

public class GDTTemplateAdViewImpl implements AdViewExt {

    static final String TAG = GDTTemplateAdViewImpl.class.getSimpleName();
    private NativeExpressADView nativeExpressADView;
    private AdResponse adResponse;
    private String id;
    private InformationClickRandomStrategy informationClickRandomStrategy;

    public GDTTemplateAdViewImpl(NativeExpressADView nativeExpressADView, AdResponse adResponse){
        this.nativeExpressADView = nativeExpressADView;
        this.adResponse = adResponse;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public View getView() {
        return nativeExpressADView;
    }

    @Override
    public void render() {
        if(nativeExpressADView != null){
            nativeExpressADView.render();
            informationClickRandomStrategy = new InformationClickRandomStrategy();
            informationClickRandomStrategy.apply(this,adResponse);
        }
    }

    @Override
    public boolean recycle() {
        Logger.i(TAG,"recycle enter");

        if(informationClickRandomStrategy!=null){
            informationClickRandomStrategy.unapply();
            informationClickRandomStrategy = null;
        }

        if(nativeExpressADView != null){
            GDTTemplateADFeedListAdHandlerImpl.allDatas.remove(nativeExpressADView);
            Logger.i(TAG,"data size = " + GDTTemplateADFeedListAdHandlerImpl.allDatas.size());
            nativeExpressADView.destroy();
            nativeExpressADView = null;
        }
        adResponse = null;
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isRecycle() {
        return nativeExpressADView == null;
    }
}
