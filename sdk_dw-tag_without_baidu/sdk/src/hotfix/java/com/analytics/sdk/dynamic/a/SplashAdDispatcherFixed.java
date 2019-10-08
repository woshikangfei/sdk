package com.analytics.sdk.dynamic.a;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.handler.AdHandler;


/**
 * 分发开屏请求与并埋点上报
 * 配合百度并发使用
 */
// TODO: 2019/6/15 埋点要在dispatch中处理？还是加一个对Handler的代理实现？
public class SplashAdDispatcherFixed extends BasicAdDispatcher {

    static final String TAG = SplashAdDispatcherFixed.class.getSimpleName();

    public static int BAIDU_AD_CONTAINER = 0;

    private SplashAdDispatcherFixed(){}

    /**
     * 分发
     */
    public static boolean dispatch(final AdRequest adRequest, AdListeneable adListeneable) {

        ViewGroup adContainer = adRequest.getAdContainer();

        FrameLayout frameLayout = new FrameLayout(adRequest.getActivity());
//        frameLayout.setAlpha(0.0f);
//        frameLayout.setBackgroundColor(Color.RED);
        BAIDU_AD_CONTAINER = View.generateViewId();
        frameLayout.setId(BAIDU_AD_CONTAINER);
        adContainer.addView(frameLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        return new SplashAdDispatcherFixed().dispatchRequest(adRequest,adListeneable);
    }

    @Override
    protected boolean isExecuteAdHandlerOnMainThread() {
        return false;
    }

    @Override
    public void executeAdHandler(final AdHandler adHandler, final AdResponse adResponse, final AdListeneable adListeneable) throws AdSdkException {
        final SplashAdListener clientSplashListener = (SplashAdListener) adListeneable;

        ViewGroup adContainer = adResponse.getClientRequest().getAdContainer();

        Logger.i(TAG,"executeAdHandler enter , adContainer = " + adContainer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Logger.i(TAG,"executeAdHandler enter , adContainer isAttachedToWindow = " + adContainer.isAttachedToWindow() + " , isShown = " + adContainer.isShown());
        }

        adHandler.handleAd(adResponse,clientSplashListener);

    }

    @Override
    public void dispatchErrorResponse(AdRequest adRequest, AdError adError, AdListeneable adListeneable) {
        final SplashAdListener clientSplashListener = (SplashAdListener) adListeneable;
        clientSplashListener.onAdError(adError);
        ReportData.obtain(adError,IReportService.Action.ACTION_AD_ERROR,AdResponse.obtain(adRequest)).startReport();
    }

}

