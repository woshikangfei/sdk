package com.analytics.sdk.view.handler.csj.feedlist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.analytics.sdk.view.widget.MediaView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;


import java.util.ArrayList;
import java.util.List;

/**
 */
public class CSJNativeADFeedListAdHandlerImpl implements AdHandler {

    static final String TAG = CSJNativeADFeedListAdHandlerImpl.class.getSimpleName();
    //广告回调监听\
    FeedListAdListener feedListAdListener;
    private Activity activity;
    //误点率
    float clickRandomRate = 0.0f;
    private H mHandler = new H();
    // 与广告有关的变量，用来显示广告素材的UI
    private TTAdNative mTTAdNative;
    //待返回的广告list
    private List<AdView> informationFlowADViews=new ArrayList<>();
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;
    private AdResponse adResponse;
    LinearLayout strategyRootLayout;

    // 与广告有关的变量，用来显示广告素材的UI
    private MediaView mMediaView;
    private ImageView mImagePoster;
    private FrameLayout mContainer;
    private RelativeLayout mADInfoContainer;
    private AQuery mAQuery;

    /**
     * 开屏填充开屏的方法，也可信息路填充开屏
     */
    public void handleAd(final AdResponse adResponse, final AdListeneable clientAdListener) throws AdSdkException{
        AdRequest adRequest = adResponse.getClientRequest();
        feedListAdListener = (FeedListAdListener) clientAdListener;
        this.activity = adRequest.getActivity();
        this.adResponse = adResponse;

        clickRandomRate = adResponse.getResponseData().getCr();

        final ConfigBeans configBeans = adResponse.getResponseData().getValidConfigBeans();

        Logger.i(TAG,"handleAd enter , " + adResponse.getClientRequest());
        Logger.i(TAG,"handleAd enter , configBeans " + configBeans);

        //穿山甲
        PublicUtils.initCSJAppId(activity,configBeans.getAppId(),configBeans.getAppName());
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(activity);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
        getData(configBeans,adResponse.getClientRequest().getAdRequestCount());

    }

    /**
     * 加载feed广告 穿山甲  source==2
     */
    private void getData( ConfigBeans beans,int counts) {
        Logger.i(TAG,"loadInformationAdWidthCSJ enter");
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        if (beans.getHeight()<=0||beans.getWidth()<=0){
            beans.setHeight(320);
            beans.setWidth(640);
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(beans.getSlotId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(beans.getWidth(), beans.getHeight())
                .setAdCount(counts)
                .build();
        //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Logger.i(TAG,"loadInformationAdWidthCSJ onError enter , message = " + message + " , code = " + code);
                onFailHandle(code,message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    onFailHandle(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY, ErrorMessage.Ad.ERROR_DATA_EMPTY);
                    Logger.i(TAG,"loadInformationAdWidthCSJ onFeedAdLoad list is empty");
                    return;
                }else{
                    Message msg = Message.obtain();
                    msg.what = MSG_INIT_AD;
                    msg.obj = ads;
                    mHandler.sendMessage(msg);
                }
            }
        });

        //添加视频的监控
        Logger.i(TAG,"loadInformationAdWidthCSJ exit");
    }



    public void onFailHandle(int errorCode,String msg) {
        Logger.i(TAG,"handleErrror enter , errorCode "+errorCode+" , msg = " + msg);
        feedListAdListener.onAdError(new com.analytics.sdk.client.AdError(errorCode,msg));
    }

    @Override
    public boolean recycle() {
        return false;
    }


    private void initAd(final TTFeedAd ad) {
        this.strategyRootLayout = (LinearLayout) adResponse.getClientRequest().getActivity().getLayoutInflater().inflate(R.layout.jhsdk_native_cjs_unified_ad,null);
        initView();
        renderAdUi(ad);
        ad.setActivityForDownloadApp(activity);
        if(ad.getImageMode()==5){
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            ad.setVideoAdListener(new TTFeedAd.VideoAdListener() {
                @Override
                public void onVideoLoad(TTFeedAd ad) {
                    feedListAdListener.onVideoLoad();
                }

                @Override
                public void onVideoError(int errorCode, int extraCode) {

                }

                @Override
                public void onVideoAdStartPlay(TTFeedAd ad) {
                    feedListAdListener.onVideoStart();
                }

                @Override
                public void onVideoAdPaused(TTFeedAd ad) {
                    feedListAdListener.onVideoPause();
                }

                @Override
                public void onVideoAdContinuePlay(TTFeedAd ad) {
                }

                public void onProgressUpdate(long l, long l1) {
                }

                public void onVideoAdComplete(TTFeedAd ttFeedAd) {

                }
            });

            //绑定广告数据、设置交互回调
            mMediaView.addView(ad.getAdView());
        }

        bindView(ad);
//        AdNativeView adNativeView = new AdNativeView(activity,adResponse,clickRandomRate);
//        adNativeView.addView(strategyRootLayout);
//        informationFlowADViews.add(adNativeView);
    }

    private void bindView(final TTFeedAd ad){
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(mContainer);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        //creativeViewList.add(adViewHolder.mCreativeButton);
        ad.registerViewForInteraction((ViewGroup) mContainer, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                feedListAdListener.onAdClicked(null);
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                feedListAdListener.onADExposed(null);
            }
        });
    }


    private void initView() {
        mMediaView = strategyRootLayout.findViewById(R.id.csj_media_view);
        mImagePoster =strategyRootLayout. findViewById(R.id.csj_img_poster);
        mADInfoContainer = strategyRootLayout.findViewById(R.id.csj_ad_info_container);
        mContainer = strategyRootLayout.findViewById(R.id.csj_native_ad_container);
        mAQuery = new AQuery(strategyRootLayout.findViewById(R.id.csj_root));
    }

    private void renderAdUi(TTFeedAd ad) {
        int patternType = ad.getImageMode();
        ;// 3=大图，2=小图，4= 组图，5视频
        if (patternType == 3||patternType==2) {
            mAQuery.id(R.id.csj_img_poster).image(ad.getImageList().get(0).getImageUrl(), false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });
            mAQuery.id(R.id.csj_text_desc).text(ad.getDescription());
        } else if (patternType ==4) {
            mAQuery.id(R.id.csj_img_1).image(ad.getImageList().get(0).getImageUrl(), false, true);
            mAQuery.id(R.id.csj_img_2).image(ad.getImageList().get(1).getImageUrl(), false, true);
            mAQuery.id(R.id.csj_img_3).image(ad.getImageList().get(2).getImageUrl(), false, true);
            mAQuery.id(R.id.csj_native_3img_title).text(ad.getTitle());
            mAQuery.id(R.id.csj_native_3img_desc).text(ad.getDescription());
        } else if (patternType == 5) {
            mAQuery.id(R.id.csj_img_poster).clear();
            mAQuery.id(R.id.csj_text_desc).text(ad.getDescription());
        }
    }

    private class H extends Handler {
        public H() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_AD:
                    informationFlowADViews.clear();
                    List<TTFeedAd> ads = (List<TTFeedAd>) msg.obj;
                    for(int i=0;i<ads.size();i++){
                        TTFeedAd ad = ads.get(i);
                        initAd(ad);
                    }
                    feedListAdListener.onAdLoaded(null);
                    break;
                case MSG_VIDEO_START:
                    mImagePoster.setVisibility(View.GONE);
                    mMediaView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}
