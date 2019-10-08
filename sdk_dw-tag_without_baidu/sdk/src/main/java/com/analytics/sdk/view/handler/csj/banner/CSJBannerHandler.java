package com.analytics.sdk.view.handler.csj.banner;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.report.IReportServiceHelper;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.analytics.sdk.view.strategy.StrategyRootLayout;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;

/**
 * 穿山甲横幅
 */
public class CSJBannerHandler extends BasicAdHandler {

    static final String TAG = CSJBannerHandler.class.getSimpleName();

    @Override
    protected EventActionList buildEventActionList() {
        return null;
    }

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {

        final Activity activity = adRequest.getActivity();

        final StrategyRootLayout strategyRootLayout = (StrategyRootLayout) adRequest.getAdContainer();
        
        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        final TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(configBeans.getSlotId()) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                Logger.i(TAG, "onAdError enter , message = " + message);
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(3 * 1000);
                strategyRootLayout.addView(bannerView);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Logger.i(TAG, "onAdClicked enter");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Logger.i(TAG, "onAdShow enter");
                    }
                });
                //（可选）设置下载类广告的下载监听
//                bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
//                        TToast.show(mContext, "点击 " + value);
                        //用户选择不喜欢原因后，移除广告展示
//                        viewById.removeAllViews();
//                        dismiss();
                    }

                    @Override
                    public void onCancel() { //点击取消

                    }
                });
            }
        });

    }


    @Override
    public boolean recycle() {
        super.recycle();
        return true;
    }
}
