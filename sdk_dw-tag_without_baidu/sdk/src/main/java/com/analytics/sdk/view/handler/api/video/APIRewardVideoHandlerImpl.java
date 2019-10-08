package com.analytics.sdk.view.handler.api.video;

import android.app.Activity;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.analytics.sdk.view.handler.csj.video.JiGuangRewardVideoHorizontalManger;
import com.analytics.sdk.view.handler.csj.video.JiGuangRewardVideoVertailManger;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.util.List;

public class APIRewardVideoHandlerImpl implements AdHandler {

    static final String TAG = com.analytics.sdk.view.handler.csj.video.CSJRewardVideoHandlerImpl.class.getSimpleName();

    private Activity activity;
    private String channelId;
    private boolean isPlayComplation;
    private ConfigBeans configBeans;
    private TTAdNative mTTAdNative;
    private String rewardName;
    private int rewardAmount;
    private String userID;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean mHasShowDownloadActive = false;
    private List<YdtAdBean> ydtAdBeanList;
    private int orientation=0;
    private JiGuangRewardVideoVertailManger rewardVideoVertailManger;
    private JiGuangRewardVideoHorizontalManger rewardVideoHorizontalManger;
    private RewardVideoAdListener clientRewardVideoAdListener;

    public APIRewardVideoHandlerImpl() {

    }

    @Override
    public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {

        AdRequest adRequest = adResponse.getClientRequest();
        this.channelId = adRequest.getCodeId();
        this.activity = adRequest.getActivity();
        this.rewardName = adRequest.getRewardName();
        this.rewardAmount = adRequest.getRewardAmount();
        this.userID = adRequest.getUserID();

        clientRewardVideoAdListener = (RewardVideoAdListener) clientAdListener;

        initAdsParams(adResponse.getResponseData());

    }

    public void initAdsParams(final ResponseData responseModel) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(responseModel==null){
                    handlererr("无广告返回");
                    return;
                }
                orientation=responseModel.getOrientation();
                //广告填充方式  filltype==1为信息流 filltype==2为开屏
                ydtAdBeanList=responseModel.getAds();
                if(responseModel.getParams()!=null&&responseModel.getParams().size()>0){
                    configBeans=responseModel.getParams().get(0);
                    PublicUtils.initCSJAppId(activity,configBeans.getAppId(),configBeans.getAppName());
                    //step1:初始化sdk
                    TTAdManager ttAdManager = TTAdManagerHolder.get();
                    //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
                    TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
                    //step3:创建TTAdNative对象,用于调用广告请求接口
                    mTTAdNative = ttAdManager.createAdNative(activity.getApplicationContext());
                    if (orientation==1){
                        loadAd(configBeans.getSlotId(), TTAdConstant.VERTICAL);
                    }else{
                        loadAd(configBeans.getSlotId(), TTAdConstant.HORIZONTAL);
                    }
                    return;
                }
                if (ydtAdBeanList!=null&&ydtAdBeanList.size()>0&&ydtAdBeanList.get(0).getMetaGroup()!=null
                        &&ydtAdBeanList.get(0).getMetaGroup().size()>0){
                    if (orientation==1){
                        rewardVideoVertailManger=new JiGuangRewardVideoVertailManger(activity,isPlayComplation,responseModel,clientRewardVideoAdListener);
                    }else{
                        rewardVideoHorizontalManger=new JiGuangRewardVideoHorizontalManger(activity,isPlayComplation,responseModel,clientRewardVideoAdListener);
                    }

                }else{
                    handlererr("无广告返回");
                }

            }
        });

    }


    public void handlererr(String msg) {
        clientRewardVideoAdListener.onAdError(new AdError(ErrorCode.Api.ERROR_NO_AD,msg));
    }

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName(rewardName) //奖励的名称
                .setRewardAmount(rewardAmount)  //奖励的数量
                .setUserID(userID)//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                handlererr(message);
                Logger.i(TAG,message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                show();
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Logger.i(TAG,"onRewardVideoAdLoad");
                mttRewardVideoAd = ad;
//                mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                       clientRewardVideoAdListener.onAdShow();
                        clientRewardVideoAdListener.onAdExposure();
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        clientRewardVideoAdListener.onAdClicked();
                    }

                    @Override
                    public void onAdClose() {
                        clientRewardVideoAdListener.onAdDismissed();
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        clientRewardVideoAdListener.onAdVideoCompleted();
                    }

                    @Override
                    public void onVideoError() {
                        clientRewardVideoAdListener.onAdError(new AdError(ErrorCode.Api.ERROR_NO_AD,"视频加载失败"));
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {

                    }

                    public void onSkippedVideo() {

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
                            PublicUtils.showToast(activity, "下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        PublicUtils.showToast(activity, "下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        PublicUtils.showToast(activity, "下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        PublicUtils.showToast(activity, "下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        PublicUtils.showToast(activity,  "安装完成，点击下载区域打开");
                    }
                });
            }
        });
    }

    private void show() {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            mttRewardVideoAd.showRewardVideoAd(activity);
            mttRewardVideoAd = null;
        }
    }

    public void destory(){
        if (rewardVideoVertailManger!=null){
            rewardVideoVertailManger.onDestory();
            rewardVideoVertailManger=null;
        }

        if (rewardVideoHorizontalManger!=null){
            rewardVideoHorizontalManger.onDestory();
            rewardVideoHorizontalManger=null;
        }
    }

    @Override
    public boolean recycle() {
        destory();
        return true;
    }
}
