package com.analytics.sdk.view.handler.csj.banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.client.banner.BannerAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.helper.TouchEventHelper;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.LogManager;
import com.analytics.sdk.view.activity.WebviewActivity;
import com.analytics.sdk.view.dispatcher.BannerAdDispatcher;
import com.analytics.sdk.view.handler.AdClickHelper;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CSJBannerHandler2Impl extends RelativeLayout implements AdHandler {

    static final String TAG = "BannerActivity";

    private ViewGroup frameLayout ;
    //计时器
    private MyCountDownTimer myCountDownTimer;
    //主布局view
    private CSJBannerHandler2Impl adBannerView;

    //广告图片view
    private ImageView ivAd;
    //广告图片bitmap
    private Bitmap adBitmap;

    //广告logo view
    private ImageView ivLogo;
    //广告logo bitmap
    private Bitmap logoBitmap;
    //广告logo的文字
    private TextView tvLogo;

    //广告来源
    private TextView tvSource;

    //广告关闭按钮
    private TextView tvClose;

    private int source=0;

    private boolean isNormal=false;

    private List<YdtAdBean> ydtAdBeanList;

    private YdtAdBean.MetaGroupBean metaGroupBean;

    private int displayWidth = AdClientContext.displayWidth;

    private int displayHeight = AdClientContext.displayHeight;

    private int frameLayoutHeight=0;

    //广告填充方式 //1=信息流填充广告,2=开屏填充广告
    private int fillType = 1;

    private int size=0;

    private int currentSize=0;

    private boolean isCSJSdk;

    @android.support.annotation.IdRes
    int TAGIMAGE = 3000;

    /**
     * VIEW模板填充
     */
    public static int TYPE_VIEW_TEMPLATE = 1;
    /**
     * 原生数据填充
     */
    public static int TYPE_NATIVE = 2;

    private Activity activity;
    private ClickLoction location;
    private AdResponse adResponse;
    private ResponseData adModel;
    private float dianjilv;
    private ConfigBeans bean;
    private BannerAdListener clientBannderAdListener;
    private String channelId;

    public CSJBannerHandler2Impl(Context context) {
        super(context);
    }

    @Override
    public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {
        adBannerView=this;
        this.adResponse = adResponse;
        this.activity = adResponse.getClientRequest().getActivity();
        location = new ClickLoction();
        clientBannderAdListener = (BannerAdListener) clientAdListener;
        frameLayout = adResponse.getClientRequest().getAdContainer();
        channelId = adResponse.getClientRequest().getCodeId();
        initAdsParams(adResponse.getResponseData());
    }

    //上报地址
    protected List<String> arrDownloadTrackUrl=new ArrayList<>();
    protected List<String> arrDownloadedTrakUrl=new ArrayList<>();
    protected List<String> arrIntallTrackUrl=new ArrayList<>();
    protected List<String> arrIntalledTrackUrl=new ArrayList<>();
    protected List<String> arrSkipTrackUrl=new ArrayList<>();
    //广点通新增字段
    protected String view_id;
    //下载APK的名字
    protected String apkName="apk";

    protected boolean isDowload=false;
    // 点击跳转URL
    protected String mClickUrl="";

    public void initAdsParams(final ResponseData responseData) {
        Logger.i(TAG,"requestRewardVideo enter");
        adModel = responseData;
        source = responseData.getSource();
        dianjilv = responseData.getCr();
        ydtAdBeanList = responseData.getAds();
        if(responseData.isSdkSource()){
            size = responseData.getParams().size();
            bean = responseData.getParams().get(0);
            source = bean.getSource();
            fillType = bean.getSlotFill();

            requestBannerAdWithSdk();

        } else if (responseData.isApiSource()){
            //初始化广告的布局
            initView();

            metaGroupBean=ydtAdBeanList.get(0).getMetaGroup().get(0);
            mClickUrl=metaGroupBean.getClickUrl();
            view_id=ydtAdBeanList.get(0).getView_id();
            arrDownloadTrackUrl=metaGroupBean.getArrDownloadTrackUrl();
            arrDownloadedTrakUrl=metaGroupBean.getArrDownloadedTrakUrl();
            arrIntallTrackUrl=metaGroupBean.getArrIntallTrackUrl();
            arrIntalledTrackUrl=metaGroupBean.getArrIntalledTrackUrl();
            arrSkipTrackUrl=metaGroupBean.getArrSkipTrackUrl();
            if (!PublicUtils.isEmpty(metaGroupBean.getBrandName())){
                apkName=metaGroupBean.getBrandName();
            }else {
                apkName = "apk";
            }

            if(!PublicUtils.isEmpty(ydtAdBeanList.get(0).getAdlogo())){
                getAdLogo(ydtAdBeanList.get(0).getAdlogo());
            }else{
                tvLogo.setVisibility(VISIBLE);
            }
            List<String> imgs = metaGroupBean.getImageUrl();
            if(imgs!=null&&imgs.size()>0){
                getBitmap(false,imgs.get(0));
            }
        }else{
            requestError();
        }
    }

    private void initView(){
        isNormal=false;
        //广告图片的显示
        if (frameLayout!=null&&frameLayout.getChildCount()>0){
            frameLayout.removeAllViews();
        }
        if (frameLayout!=null){
            frameLayoutHeight=frameLayout.getHeight();
        }
        if (frameLayoutHeight==0){
            frameLayoutHeight=150*displayHeight/1920;
        }
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,frameLayoutHeight);
        adBannerView.setLayoutParams(layoutParams);
        ivAd = new ImageView(activity);
        ivAd.setId(TAGIMAGE);
        ivAd.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivAd.setLayoutParams(imageParams);
        addView(ivAd);

        //API需要
        TouchEventHelper.bind(ivAd, new TouchEventHelper.OnPerfromClickListener() {
            @Override
            public void onClick(View view, TouchEventHelper touchEventHelper) {

                try {
                    AdClickHelper.handleClick(adResponse,adResponse.getResponseData().getValidFristYdtAdData(),touchEventHelper.getClickLoction());
                } catch (AdSdkException e) {
                    e.printStackTrace();
                }

            }
        });

        //广告Logo
        ivLogo =new ImageView(activity);
        RelativeLayout.LayoutParams adLogoParams = new RelativeLayout.LayoutParams(24*displayWidth/1080, 24*displayWidth/1080);
        adLogoParams.addRule(RelativeLayout.ALIGN_BOTTOM,TAGIMAGE);
        adLogoParams.addRule(RelativeLayout.ALIGN_RIGHT,TAGIMAGE);
        ivLogo.setLayoutParams(adLogoParams);
        ivLogo.setAlpha(0.5f);
        addView(ivLogo);
        ivLogo.setVisibility(View.GONE);


        //广告的TextView logo
        tvLogo=new TextView(activity);
        RelativeLayout.LayoutParams adLogoTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        adLogoTextParams.addRule(RelativeLayout.ALIGN_BOTTOM,TAGIMAGE);
        adLogoTextParams.addRule(RelativeLayout.ALIGN_RIGHT,TAGIMAGE);
        tvLogo.setLayoutParams(adLogoTextParams);
        tvLogo.setTextSize(10);
        tvLogo.setText("广告");
        tvLogo.setTextColor(Color.parseColor("#8C8C8C8C"));
        addView(tvLogo);
        tvLogo.setVisibility(View.GONE);

        tvSource = new TextView(activity);
        RelativeLayout.LayoutParams advertisingSourcepParams= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        advertisingSourcepParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        advertisingSourcepParams.addRule(RelativeLayout.ALIGN_BOTTOM,TAGIMAGE);
        int left=60*displayWidth/1080;
        int right=60*displayWidth/1080;
        tvSource.setBackgroundColor(Color.parseColor("#99666666"));
        tvSource.setGravity(Gravity.CENTER_HORIZONTAL);
        tvSource.setPadding(left,5,right,5);
        tvSource.setLayoutParams(advertisingSourcepParams);
        tvSource.setTextSize(12);
        tvSource.setTextColor(Color.parseColor("#ffffff"));
        tvSource.setSingleLine(true);
        tvSource.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        addView(tvSource);

        //关闭按钮的显示
        createCloseBtn();
    }

    private void createCloseBtn() {
        tvClose = new TextView(activity);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvClose.setLayoutParams(closeParams);
        tvClose.setBackgroundColor(Color.parseColor("#00000000"));
        tvClose.setPadding(10,0,10,0);
        tvClose.setTextSize(18);
        tvClose.setText("×");
        tvClose.setTextColor(Color.parseColor("#FFFFFF"));
        addView(tvClose);
        if(!SdkHelper.isHit(dianjilv)){
            isNormal=true;
            if (isCSJSdk){
                return;
            }
            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestError();
                    BeanUtils.track(arrSkipTrackUrl,activity,location);

                    clientBannderAdListener.onAdDismissed();

                }
            });
        }
    }

    private void requestError() {
        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }
        if (adBannerView!=null){
            adBannerView.setVisibility(View.GONE);
        }
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }
        if (logoBitmap!=null&&!logoBitmap.isRecycled()){
            logoBitmap.recycle();
            logoBitmap=null;
        }
    }

    public void handlError(int code,String msg) {
        clientBannderAdListener.onAdError(new com.analytics.sdk.client.AdError(code,msg));
    }

    public void getBitmap(final boolean isSdk,String imageUrl){
        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
                requestError();

                if(!isSdk){
                    handlError(ErrorCode.Api.ERROR_LOAD_AD_IMAGE,ErrorMessage.Ad.ERROR_GET_IMAGE);
                } else {
                    handlError(ErrorCode.SDKCSJ.ERROR_LOAD_AD_IMAGE,ErrorMessage.Ad.ERROR_GET_IMAGE);
                }

            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                adBitmap = BitmapFactory.decodeStream(stream);
                // 重新设置图片大小
                adBitmap = resizeImage(adBitmap);
                if(adBitmap !=null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PublicUtils.showImage(activity,ivAd,adBitmap);
                            show();
                        }
                    });
                }
            }
        });
    }


    // 设置图片缩放
    private Bitmap resizeImage(Bitmap bitmap) {
        // 获得bitmap的宽
        int width = bitmap.getWidth();
        // 获得bitmap的高
        int height = bitmap.getHeight();
        location.setFinalwidth(displayWidth);
        float scaleWidth=(float) displayWidth/width;
        int finalHeight=(int) (scaleWidth*height);
        location.setFinalHeight(finalHeight);
        Matrix matrix = new Matrix();
        // 缩放图片
        matrix.postScale(scaleWidth, scaleWidth);
        return  Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

    }

    public void getAdLogo(String imageUrl){

        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
                tvLogo.setVisibility(VISIBLE);
            }

            @Override
            public void onSuccess(InputStream stream) {
                logoBitmap= BitmapFactory.decodeStream(stream);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ivLogo!=null){
                            PublicUtils.showImage(activity,ivLogo,logoBitmap);
                        }else{
                            tvLogo.setVisibility(VISIBLE);
                        }
                    }
                });
            }
        });
    }
    

    private void show() {
        if (metaGroupBean==null){
            return;
        }
        frameLayout.addView(adBannerView);

        if (!PublicUtils.isEmpty(metaGroupBean.getAdTitle())){
            tvSource.setVisibility(View.VISIBLE);
            tvSource.setText(metaGroupBean.getAdTitle());
        }else{
            tvSource.setVisibility(View.GONE);
        }
        if (adBannerView!=null){
            adBannerView.setVisibility(View.VISIBLE);
        }

        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }
        if (isCSJSdk){
            LogManager.updatelog(adResponse.getClientRequest().getRequestId(),AdEventActions.ACTION_AD_SHOW,IReportService.Type.TYPE_SDK,AdType.BANNER.getStringValue(),channelId,source);
            return;
        }

        clientBannderAdListener.onAdShow();
        BeanUtils.track(adModel.getAds().get(0).getMetaGroup().get(0).getWinNoticeUrls(),activity,location);
        myCountDownTimer=new MyCountDownTimer(30*1000,1000);
        myCountDownTimer.start();

        clientBannderAdListener.onAdExposure();
    }


    private void requestBannerAdWithSdk() {
        Logger.i(TAG,"requestBannerAdWithSdk enter");
        currentSize++;
        if(source == DataSource.SDK_CSJ){//穿山甲
            isCSJSdk = true;
            PublicUtils.initCSJAppId(activity,bean.getAppId(),bean.getAppName());
            initCSJView();
            //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
            mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
            //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
            if (fillType==TYPE_VIEW_TEMPLATE){//模板填充
                loadBannerAd(bean.getSlotId());
            }else{//原生填充
                loadBannerNativeAdWithCSJ(bean.getSlotId());
            }
        }

    }

    private void initCSJView() {
        Logger.i(TAG,"initCSJView enter");
        if (adBannerView!= null) {
            ViewGroup parentViewGroup = (ViewGroup) adBannerView.getParent();
            if (parentViewGroup != null ) {
                parentViewGroup.removeView(adBannerView);
            }
        }
        if (adBannerView!=null){
            adBannerView.setVisibility(GONE);
        }
        Logger.i(TAG,"initCSJView exit");
    }


    /**
     * 穿山甲原生SDK
     * */
    private void loadBannerNativeAdWithCSJ(String codeId) {
        //step4:创建广告请求参数AdSlot,注意其中的setNativeAdtype方法，具体参数含义参考文档
        Logger.i(TAG,"loadBannerNativeAdWithCSJ enter");
        try {
            if (bean.getHeight()<=0||bean.getWidth()<=0){
                bean.setHeight(100);
                bean.setWidth(640);
            }
            final AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(bean.getWidth(), bean.getHeight())
                    .setNativeAdType(AdSlot.TYPE_BANNER) //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                    .setAdCount(1)
                    .build();

            //step5:请求广告，对请求回调的广告作渲染处理
            mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
                @Override
                public void onError(int code, String message) {
                    Logger.i(TAG,"loadBannerNativeAdWithCSJ onError enter ,  message = " + message);
                    onFailHandle(code,ErrorMessage.Ad.ERROR_GET_ADS);
                }

                @Override
                public void onNativeAdLoad(List<TTNativeAd> ads) {
                    Logger.i(TAG,"loadBannerNativeAdWithCSJ onNativeAdLoad enter");
                    if (ads==null||ads.get(0) == null) {
                        onFailHandle(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY,ErrorMessage.Ad.ERROR_DATA_EMPTY);
                        return;
                    }
                    //绑定原生广告的数据
                    setCSJBannerAdData(ads.get(0));
                }
            });
        }catch (Exception e){
            onFailHandle(ErrorCode.SDKCSJ.ERROR_HANDLE,ErrorMessage.Ad.ERROR_HANDLE);
        }

    }

    private void setCSJBannerAdData(TTNativeAd ttNativeAd) {
        Logger.i(TAG,"setCSJBannerAdData enter");
        initView();
        isDowload=false;
        YdtAdBean ydtAdBean=new YdtAdBean();
        List<YdtAdBean.MetaGroupBean> metaGroupBeans=new ArrayList<>();
        metaGroupBean=new YdtAdBean.MetaGroupBean();
        metaGroupBean.setAdTitle(ttNativeAd.getTitle());
        List<String> imageList=new ArrayList<>();
        if (ttNativeAd.getImageList()!=null&&ttNativeAd.getImageList().size()>0){
            imageList.add(ttNativeAd.getImageList().get(0).getImageUrl());
        }
        metaGroupBean.setImageUrl(imageList);
        metaGroupBean.setAppSize(ttNativeAd.getAppSize());
        List<String> descList=new ArrayList<>();
        descList.add(ttNativeAd.getDescription());
        metaGroupBean.setDescs(descList);
        List<String> iconList=new ArrayList<>();
        iconList.add(ttNativeAd.getIcon().getImageUrl());
        metaGroupBean.setIconUrls(iconList);
        metaGroupBean.setInteractionType(ttNativeAd.getInteractionType());
        metaGroupBeans.add(metaGroupBean);
        ydtAdBean.setMetaGroup(metaGroupBeans);
        ydtAdBeanList=new ArrayList<>();
        ydtAdBeanList.add(ydtAdBean);
        if(!PublicUtils.isEmpty(ydtAdBeanList.get(0).getAdlogo())){
            getAdLogo(ydtAdBeanList.get(0).getAdlogo());
        }else{
            tvLogo.setVisibility(VISIBLE);
        }
        List<String> imgs = metaGroupBean.getImageUrl();
        if(imgs!=null&&imgs.size()>0){
            getBitmap(true,imgs.get(0));
        }

        //可根据广告类型，为交互区域设置不同提示信息
        switch (ttNativeAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                ttNativeAd.setActivityForDownloadApp(activity);
                ttNativeAd.setDownloadListener(mDownloadListener); // 注册下载监听器
                break;
        }
        //可以被点击的view, 也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(ivAd);
        View view=null;
        if (isNormal){
            view=tvClose;
            bindDislikeAction(ttNativeAd, view);
        }
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ttNativeAd.registerViewForInteraction((ViewGroup) frameLayout, clickViewList, null, view, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                Logger.i(TAG,"onAdClicked enter");
                clientBannderAdListener.onAdClicked();
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                Logger.i(TAG,"onAdCreativeClick enter");
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                Logger.i(TAG,"onAdShow enter");
                clientBannderAdListener.onAdShow();
            }
        });
    }



    //接入网盟的dislike 逻辑，有助于提示广告精准投放度
    private void bindDislikeAction(TTNativeAd ad, View dislikeView) {
        final TTAdDislike ttAdDislike = ad.getDislikeDialog(activity);
        if (ttAdDislike != null) {
            ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    frameLayout.removeAllViews();
                }

                @Override
                public void onCancel() {
                    clientBannderAdListener.onAdDismissed();
                }
            });
        }
        dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttAdDislike != null)
                    ttAdDislike.showDislikeDialog();
            }
        });
    }
    private final TTAppDownloadListener mDownloadListener = new TTAppDownloadListener() {
        @Override
        public void onIdle() {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
        }

        @Override
        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
        }

        @Override
        public void onInstalled(String fileName, String appName) {
        }

        @Override
        public void onDownloadFinished(long totalBytes, String fileName, String appName) {

        }
    };

    /**
     * 穿山甲SDK
     * */
    private TTAdNative mTTAdNative;

    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        try {
            if (bean.getHeight()<=0||bean.getWidth()<=0){
                bean.setHeight(100);
                bean.setWidth(640);
            }
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(codeId) //广告位id
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(bean.getWidth(), bean.getHeight())
                    .build();
            //step5:请求广告，对请求回调的广告作渲染处理
            mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

                @Override
                public void onError(int code, String message) {
                    onFailHandle(code,ErrorMessage.Ad.ERROR_GET_ADS);
                    frameLayout.removeAllViews();
                }

                @Override
                public void onBannerAdLoad(final TTBannerAd ad) {
                    if (ad == null) {
                        onFailHandle(ErrorCode.SDKCSJ.ERROR_NO_AD,ErrorMessage.Ad.ERROR_DATA_EMPTY);
                        return;
                    }
                    View bannerView = ad.getBannerView();
                    if (bannerView == null) {
                        onFailHandle(ErrorCode.SDKCSJ.ERROR_NO_AD,ErrorMessage.Ad.ERROR_DATA_EMPTY);
                        return;
                    }
                    adBannerView.setVisibility(VISIBLE);
                    //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                    ad.setSlideIntervalTime(30 * 1000);
                    frameLayout.removeAllViews();
                    frameLayout.addView(bannerView);
                    //设置广告互动监听回调
                    ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            clientBannderAdListener.onAdClicked();
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            clientBannderAdListener.onAdShow();
                            clientBannderAdListener.onAdExposure();
                        }
                    });
                    //（可选）设置下载类广告的下载监听
                    bindDownloadListener(ad);
                    //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                    ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                            //用户选择不喜欢原因后，移除广告展示
                            frameLayout.removeAllViews();
                        }

                        @Override
                        public void onCancel() {
                            clientBannderAdListener.onAdDismissed();
                        }
                    });
                }
            });
        }catch (Exception e){
            onFailHandle(ErrorCode.SDKCSJ.ERROR_HANDLE,ErrorMessage.Ad.ERROR_HANDLE);
        }
    }

    private boolean mHasShowDownloadActive = false;

    private void bindDownloadListener(TTBannerAd ad) {
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //PublicUtils.showToast(activity, "点击图片开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                 //   PublicUtils.showToast(activity,  "下载中，点击图片暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
             //   PublicUtils.showToast(activity, "下载暂停，点击图片继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
               // PublicUtils.showToast(activity, "下载失败，点击图片重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
               // PublicUtils.showToast(activity, "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                //PublicUtils.showToast(activity, "点击图片安装");
            }
        });
    }

    @Override
    public boolean recycle() {
        destory();
        return true;
    }

    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            BannerAdDispatcher.dispatch(adResponse.getClientRequest(),clientBannderAdListener);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }


    private void onFailHandle(int code,String message){
        if (size >=2 && currentSize == 1){
            bean = adModel.getParams().get(1);
            fillType = bean.getSlotFill();
            source = bean.getSource();
            requestBannerAdWithSdk();
        }else{
            handlError(code,message);
        }
    }
    /**
     * 跳转到落地页
     * */
    protected void jumpToWebview(){
        Intent intent=new Intent(activity,WebviewActivity.class);
        intent.putExtra("mClickUrl",mClickUrl);
        String title=metaGroupBean.getAdTitle()==null?"":metaGroupBean.getAdTitle();
        intent.putExtra("title",title);
        activity.startActivity(intent);
    }


    public void destory(){
        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }
        if (logoBitmap!=null&&!logoBitmap.isRecycled()){
            logoBitmap.recycle();
            logoBitmap=null;
        }

    }
}


