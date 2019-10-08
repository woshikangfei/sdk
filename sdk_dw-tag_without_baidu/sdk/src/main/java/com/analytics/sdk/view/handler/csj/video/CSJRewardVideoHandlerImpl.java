package com.analytics.sdk.view.handler.csj.video;

import android.app.Activity;
import android.content.Context;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.AdErrorFactory;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

public class CSJRewardVideoHandlerImpl extends BasicAdHandler {

    static final String TAG = "CSJRewardVideoHandlerImpl";

    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mHasShowDownloadActive = false;

    public CSJRewardVideoHandlerImpl() {
    }

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER
                .clone()
                .addActionList(AdEventActions.BASE_REWARD_VIDEO)
                .addActionList(AdEventActions.BASE_DOWNLOAD);
    }

    @Override
    protected void onHandleAd(AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {

        ResponseData responseData = adResponse.getResponseData();
        final int orientation = responseData.getOrientation();

        if (orientation == TTAdConstant.VERTICAL){
            loadAdWithOrientation(adRequest,configBeans, TTAdConstant.VERTICAL);
        }else{
            loadAdWithOrientation(adRequest,configBeans, TTAdConstant.HORIZONTAL);
        }

    }

    private void initCSJAppId(Context context, String appId, String appName){
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常穿山甲
        PublicUtils.initCSJAppId(context,appId,appName);
    }

    private void loadAdWithOrientation(AdRequest adRequest, ConfigBeans configBeans, int orientation) {

        final Activity activity = adRequest.getActivity();

        initCSJAppId(activity,configBeans.getAppId(),configBeans.getAppName());
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(configBeans.getSlotId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .setRewardName(adRequest.getRewardName()) //奖励的名称
                    .setRewardAmount(adRequest.getRewardAmount())  //奖励的数量
                    .setUserID(adRequest.getUserID())//用户id,必传参数
                    .setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();

        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(activity.getApplicationContext());

        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {

                AdError adError = new AdError(code,message);
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));

            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED,adResponse));
                show(activity);
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Logger.i(TAG,"onRewardVideoAdLoad");
                mttRewardVideoAd = ad;

                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        onRequestEnd();
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
                    }

                    @Override
                    public void onAdClose() {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED,adResponse));
                    }

                    @Override
                    public void onVideoError() {
                        AdError adError = AdErrorFactory.factory().create(ErrorCode.RewardVideo.ERROR_VIDEO_LOAD);
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adError));
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD,adResponse));
                    }

                    public void onSkippedVideo() {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_SKIPPED,adResponse));
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            EventScheduler.dispatch(Event.obtain(AdEventActions.Download.ACTION_AD_ACTIVE,adResponse));
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.Download.ACTION_AD_PAUSE,adResponse));
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.Download.ACTION_AD_ERROR,adResponse));
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.Download.ACTION_AD_COMPLETED,adResponse));
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.Download.ACTION_AD_INSTALLED,adResponse));
                    }
                });

                show(activity);

            }
        });
    }

    private void show(Activity activity) {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            mttRewardVideoAd.showRewardVideoAd(activity);
            mttRewardVideoAd = null;
        }
    }


    @Override
    public boolean recycle() {
        super.recycle();
        return true;
    }
}
