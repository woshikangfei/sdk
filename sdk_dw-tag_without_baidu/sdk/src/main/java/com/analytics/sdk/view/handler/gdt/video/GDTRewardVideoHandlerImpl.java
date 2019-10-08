package com.analytics.sdk.view.handler.gdt.video;

import android.app.Activity;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.common.BasicAdHandler;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;

/**
 * 广点通的奖励视频
 */
public class GDTRewardVideoHandlerImpl extends BasicAdHandler {

    static final String TAG = "GDTRewardVideoHandlerImpl";

    private RewardVideoAD rewardVideoAD;
    /**
     * 广点通DEMO中就是这样定义的
     */
    private boolean adLoaded = false;
    private boolean adVideoCached = false;

    public GDTRewardVideoHandlerImpl() {
    }

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER.clone()
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_LOADED)
                            .addAction(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED);
    }

    @Override
    protected void onHandleAd(final AdResponse adResponse, AdListeneable clientAdListener,final ConfigBeans configBeans) throws AdSdkException {

        final Activity activity = adRequest.getActivity();

        // 1. 初始化激励视频广告
        rewardVideoAD = new RewardVideoAD(activity, configBeans.getAppId(), configBeans.getSlotId(), new RewardVideoADListener(){

            @Override
            public void onADLoad() { //广告加载成功，可在此回调后进行广告展示，此时广告过期时间确定，可通过RewardVideoAD.getExpireTimestamp()获取
                Logger.i(TAG,"onADLoad enter");
                adLoaded = true;
                EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_LOADED,adResponse));
                showAds();
            }

            @Override
            public void onVideoCached() { //视频素材缓存成功，可在此回调后进行广告展示
                Logger.i(TAG,"onVideoCached enter");
                adVideoCached = true;
                EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_CACHED,adResponse));
                showAds();
            }

            @Override
            public void onADShow() { //激励视频广告页面展示，此后RewardVideoAD.hasShown()返回true
                Logger.i(TAG,"onADShow enter");
                onRequestEnd();
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW,adResponse));
            }

            @Override
            public void onADExpose() { //激励视频广告曝光
                Logger.i(TAG,"onADExpose enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse));
            }

            @Override
            public void onReward() { //激励视频广告激励发放
                Logger.i(TAG,"onReward enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_REWARD,adResponse));
            }

            @Override
            public void onADClick() {
                Logger.i(TAG,"onADClick enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse));
            }

            @Override
            public void onVideoComplete() { //广告视频素材播放完毕
                Logger.i(TAG,"onVideoComplete enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.RewardVideo.ACTION_AD_VIDEO_COMPLETED,adResponse));
            }

            @Override
            public void onADClose() { //激励视频广告被关闭
                Logger.i(TAG,"onADClose enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse));
            }

            @Override
            public void onError(com.qq.e.comm.util.AdError adError) {
                AdError adGQTError = new AdError(adError.getErrorCode(),adError.getErrorMsg());
                Logger.i(TAG,"onError enter , error = " + adGQTError);
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,adGQTError));
            }
        });

        adLoaded = false;
        adVideoCached = false;
        rewardVideoAD.loadAD();

    }

    private void showAds() {
        // 3. 展示激励视频广告
        if (adLoaded && rewardVideoAD != null){
            //广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
            if (!rewardVideoAD.hasShown()) {
                //广告展示检查2：当前广告数据还没有展示过
                rewardVideoAD.showAD();
//                long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
//                //广告展示检查3：展示广告前判断广告数据未过期
//                if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
//
//                } else {
//                    PublicUtils.showToast(activity, "激励视频广告已过期，请再次请求广告后进行广告展示！");
//                }

                rewardVideoAD = null;

            }
        }
    }

    @Override
    public boolean recycle() {
        super.recycle();
        if(rewardVideoAD != null){
            rewardVideoAD = null;
        }
        return true;
    }
}
