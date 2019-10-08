package com.analytics.sdk.view.handler.gdt.banner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.analytics.sdk.view.strategy.StrategyRootLayout;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;

/**
 * 广点通横幅
 */
public class GDTBannerHandlerImpl extends BasicAdHandler {

    static final String TAG = GDTBannerHandlerImpl.class.getSimpleName();

    /**
     * 开屏填充开屏的方法，也可信息路填充开屏
     */
    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER.clone().addActionList(AdEventActions.BASE_BANNER);
    }

    @Override
    protected void onHandleAd(final AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadBannder(adResponse);
            }
        });
    }

    UnifiedBannerView gdtBannderView;

    private void loadBannder(final AdResponse adResponse) {
        Logger.i(TAG,"getBannerViewWithGDT enter");

        final Activity activity = adResponse.getClientRequest().getActivity();
        final StrategyRootLayout strategyRootLayout = (StrategyRootLayout) adResponse.getClientRequest().getAdContainer();

        gdtBannderView = new UnifiedBannerView(activity, configBeans.getAppId(), configBeans.getSlotId(), new UnifiedBannerADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                Logger.i(TAG,"onNoAD, msg = " + adError.getErrorMsg());
                AdError newAdError = new AdError(adError.getErrorCode(),adError.getErrorMsg());
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,newAdError));
            }

            @Override
            public void onADReceive() { //广告加载成功回调，表示广告相关的资源已经加载完毕，Ready To Show
                Logger.i(TAG,"onADReceive enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
            }

            @Override
            public void onADExposure() {
                Logger.i(TAG,"onADExposure enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
                Context context = strategyRootLayout.getContext();

                int paddingRight = UIHelper.dip2px(context,0);
                int paddingTop = UIHelper.dip2px(context,0);
                int closeBtnWidth = UIHelper.dip2px(context,40);
                int closeBtnHeight = UIHelper.dip2px(context,40);

                strategyRootLayout.apply(gdtBannderView,adResponse,paddingTop,paddingRight,closeBtnWidth,closeBtnHeight);

            }

            @Override
            public void onADClosed() {
                Logger.i(TAG,"onADClosed enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
            }

            @Override
            public void onADClicked() {
                Logger.i(TAG,"onADClicked enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
            }

            @Override
            public void onADLeftApplication() { //由于广告点击离开 APP 时调用
                Logger.i(TAG,"onADLeftApplication enter");
            }

            @Override
            public void onADOpenOverlay() { //当广告打开浮层时调用，如打开内置浏览器、内容展示浮层，一般发生在点击之后
                Logger.i(TAG,"onADOpenOverlay enter");
            }

            @Override
            public void onADCloseOverlay() { //浮层关闭时调用
                Logger.i(TAG,"onADCloseOverlay enter");
            }
        });

        strategyRootLayout.addView(gdtBannderView,getUnifiedBannerLayoutParams(activity));
        gdtBannderView.setRefresh(adRequest.getRefresh());
        gdtBannderView.loadAD();
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     * @return
     */
    private ViewGroup.LayoutParams getUnifiedBannerLayoutParams(Activity activity) {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new ViewGroup.LayoutParams(screenSize.x,  Math.round(screenSize.x / 6.4F));
    }


    @Override
    public boolean recycle() {
        super.recycle();
        if(gdtBannderView != null){
            gdtBannderView.destroy();
            gdtBannderView = null;
        }
        return true;
    }
}
