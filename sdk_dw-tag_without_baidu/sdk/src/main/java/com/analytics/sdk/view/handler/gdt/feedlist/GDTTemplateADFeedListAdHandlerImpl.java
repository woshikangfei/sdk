package com.analytics.sdk.view.handler.gdt.feedlist;

import android.app.Activity;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.feedlist.AdSize;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 广点通信息流
 * 模板的实现
 */
public class GDTTemplateADFeedListAdHandlerImpl extends BasicAdHandler {

    static final String TAG = "GDTTemplateADFeedListAdHandlerImpl";

    private NativeExpressAD mADManager;
    //待返回的广告list
    private List<AdView> feedlistAdViews = new ArrayList<>();

    static HashMap<NativeExpressADView,AdView> allDatas = new HashMap<>();

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER.clone().addActionList(AdEventActions.BASE_FEEDLIST);
    }

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {
        requestFeedList(adResponse.getClientRequest().getActivity(),configBeans);
    }

    /**
     * 广点通
     * */
    private void requestFeedList(final Activity activity,ConfigBeans beans) {
         // 消息流中用AUTO_HEIGHT
        AdSize adSize = adRequest.getAdSize();
        final ADSize adSizeGDT = new ADSize(adSize.getAdWith(), adSize.getAdHeight());

        mADManager = new NativeExpressAD(activity, adSizeGDT, beans.getAppId(), beans.getSlotId(), new NativeExpressAD.NativeExpressADListener() {

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                if(list == null){
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,new AdError(ErrorCode.FeedList.ERROR_VIDEO_SHOWN,"数据为空")));
                    return;
                }
                feedlistAdViews.clear();
                int size = list.size();
                for (int i = 0;i < list.size();i++){

                    NativeExpressADView nativeExpressADView = list.get(i);

//                    nativeExpressADView.setAdSize(adSizeGDT);

                    AdView adView = new GDTTemplateAdViewImpl(nativeExpressADView,adResponse);
                    feedlistAdViews.add(adView);

                    allDatas.put(nativeExpressADView,adView);

                }

                EventScheduler.dispatch(Event.obtain(AdEventActions.FeedList.ACTION_AD_LOADED,adResponse.setResponseFeedlistCount(size),feedlistAdViews));
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onRenderFail()");
                EventScheduler.dispatch(Event.obtain(AdEventActions.FeedList.ACTION_AD_RENDER_FAIL,adResponse,new GDTTemplateAdViewImpl(nativeExpressADView,adResponse)));
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onRenderSuccess()");
                EventScheduler.dispatch(Event.obtain(AdEventActions.FeedList.ACTION_AD_RENDER_SUCCESS,adResponse,new GDTTemplateAdViewImpl(nativeExpressADView,adResponse)));
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onADExposure()");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse,new GDTTemplateAdViewImpl(nativeExpressADView,adResponse)));
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onADClicked enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse,new GDTTemplateAdViewImpl(nativeExpressADView,adResponse)));
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                AdView adView = allDatas.get(nativeExpressADView);

                Logger.i(TAG,"onADClosed enter , mapping adView = " + adView);

                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse,adView));

                if(adView != null){
                    allDatas.remove(nativeExpressADView);
                }
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onADLeftApplication enter");
            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onADOpenOverlay enter");
            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                Logger.i(TAG,"onADCloseOverlay enter");
            }

            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                AdError newAdError = new AdError(adError.getErrorCode(),adError.getErrorMsg());
                Logger.i(TAG,"onNoAD enter , " + newAdError);

                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,newAdError));
            }
        });
        mADManager.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //

        int adRequestCount = adResponse.getClientRequest().getAdRequestCount();

        mADManager.loadAD(adRequestCount);
    }


    @Override
    public boolean recycle() {
        super.recycle();
        return true;
    }

}
