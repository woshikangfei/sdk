package com.analytics.sdk.view.handler.csj.interstitial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.client.interstitial.InterstitialAdListener;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.LogManager;
import com.analytics.sdk.view.activity.WebviewActivity;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.BaseHandlerImpl;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.bytedance.sdk.openadsdk.AdSlot.Builder;


/**
 * Created by kf on 2018/9/17.
 */

//插屏
public class CSJInterstitialHandlerImpl extends BaseHandlerImpl implements AdHandler {

    static final String TAG = "CSJInterstitialHandlerImpl";

    //主布局
    private RelativeLayout layout;
    //弹窗
    private ConfirmDialog dialog;
    //关闭按钮
    private  TextView tvClose;
    //广告图片
    private ImageView ivAd;
    //广告图片 bitmap
    private Bitmap adBitmap;
    //广告logo
    private ImageView ivLogo;
    //广告logo bitmap
    private Bitmap logoBitmap;
    private TextView  tvLogo;
    //广告来源
    private TextView tvSource;

    private boolean isSdk = false;

    //广告填充方式 //1=信息流填充广告,2=原生充广告
    private int fillType =1;


    private int size=0;

    private int currentSize=0;

    private List<YdtAdBean>  ydtAdBeanList;
    private YdtAdBean.MetaGroupBean metaGroupBean;

    @android.support.annotation.IdRes
    int TAGIMAGE = 2000;

    /**
     * 原生填充
     */
    public static int TYPE_NATIVE = 2;
    /**
     * 信息流填充
     */
    public static int TYPE_INFORMATION = 1;


    InterstitialAdListener interstitialAdListener;
    private AdResponse adResponse;

    public CSJInterstitialHandlerImpl(Context context) {
        super(context);
    }

    @Override
    public void handleAd(AdResponse adResponse, AdListeneable clientAdListener) throws AdSdkException {
        this.interstitialAdListener = (InterstitialAdListener) clientAdListener;
        this.activity = adResponse.getClientRequest().getActivity();
        this.adResponse = adResponse;
        location = new ClickLoction();
        initAdsParams(adResponse.getResponseData());
    }

    public void initAdsParams(final ResponseData responseModel) {

        //广告填充方式  filltype==1为信息流 filltype==2为开屏
        dianjilv = responseModel.getCr();
        ydtAdBeanList = responseModel.getAds();
        source = responseModel.getSource();
        adModel = responseModel;
        if(responseModel.isSdkSource()){
            size=responseModel.getParams().size();
            bean = responseModel.getParams().get(0);
            fillType = bean.getSlotFill();
            source = bean.getSource();

            Logger.i(TAG,"*SDK* LogManager.log = true , dianjilv = " + dianjilv + " , sourceName = " + SdkHelper.getSourceName(source) + " , fillType = " + fillType + ", paramSize = " + size);

            adInterstitialSDKShow();
        } else if(responseModel.isApiSource()){
            isDowload=false;
            apiOrsdkType=1;
            metaGroupBean=ydtAdBeanList.get(0).getMetaGroup().get(0);
            fillType = ydtAdBeanList.get(0).getFillType();
            mClickUrl=metaGroupBean.getClickUrl();
            view_id=ydtAdBeanList.get(0).getView_id();

            Logger.i(TAG,"*API* LogManager.log = true , dianjilv = " + dianjilv + " , sourceName = " + SdkHelper.getSourceName(source) + " , fillType = " + fillType + ", paramSize = " + size);

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
            }

            List<String>  imgs = metaGroupBean.getImageUrl();
            if(imgs!=null&&imgs.size()>0){

                String adImage = imgs.get(0);
                if(TextUtils.isEmpty(adImage)) {
                    handleErrror(ErrorCode.Api.ERROR_NO_AD,ErrorMessage.Ad.ERROR_IMAGE_NOT_FOUND);
                } else {
                    getBitmap(adImage);
                }

            }

        }
    }

    public void handleErrror(int errorCode,String msg) {
        interstitialAdListener.onAdError(new com.analytics.sdk.client.AdError(errorCode,msg));
    }

    public void getBitmap(final String imageUrl){
        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
                if(apiOrsdkType == 1){
                    handleErrror(ErrorCode.Api.ERROR_LOAD_AD_IMAGE,ErrorMessage.Ad.ERROR_GET_IMAGE);
                } else {
                    handleErrror(ErrorCode.SDKCSJ.ERROR_LOAD_AD_IMAGE,ErrorMessage.Ad.ERROR_GET_IMAGE);
                }
            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                adBitmap = BitmapFactory.decodeStream(stream);
                // 重新设置图片大小
                adBitmap = resizeImage(adBitmap);
                updateUi();
            }
        });
    }

    // 设置图片缩放
    private Bitmap resizeImage(Bitmap bitmap) {
        // 获得bitmap的宽
        int width = bitmap.getWidth();
        // 获得bitmap的高
        int height = bitmap.getHeight();
        float scaleWidth=(float) displayWidth/width;
        float newscaleheight =scaleWidth*height;
        location.setFinalHeight((int)newscaleheight);
        location.setFinalwidth(displayWidth);
        Matrix matrix = new Matrix();
        // 缩放图片
        matrix.postScale(scaleWidth, scaleWidth);
        return  Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

    }

    public void getAdLogo(final String imageUrl){
        if (PublicUtils.isEmpty(imageUrl)){
            return;
        }
        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onSuccess(InputStream stream) {
                logoBitmap= BitmapFactory.decodeStream(stream);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(logoBitmap!=null&&ivLogo!=null){
                            PublicUtils.showImage(activity, ivLogo,logoBitmap);
                        }else{
                            tvLogo.setVisibility(GONE);
                        }
                    }
                });
            }
        });
    }


    private void updateUi(){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG,"updateUi");
                dialog = new ConfirmDialog(activity);
                dialog.show();
            }
        });
    }


    public void touTiaoClick() {
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(ivAd);
        Logger.e("information","onclick");

        ad.registerViewForInteraction((ViewGroup)layout, clickViewList, null, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                interstitialAdListener.onAdClicked();
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                    closeDialog();
                }
                Logger.e("adNativ","onAdClicked");
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                Logger.e("adNativ","onAdCreativeClick");
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                interstitialAdListener.onAdShow();
                interstitialAdListener.onAdExposure();
                Logger.e("adNativ","onAdShow");
            }
        });


        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                ad.setActivityForDownloadApp(activity);
                break;
        }
    }

    @Override
    public boolean recycle() {
        destory();
        return true;
    }

    public class ConfirmDialog extends Dialog {

        private Activity context;

        public ConfirmDialog(Activity context) {
            super(context);
            this.context = context;
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setCanceledOnTouchOutside(false);
            layout= new RelativeLayout(context);
            setContentView(layout);
            //广告图片的显示
            ivAd = new ImageView(activity);
            ivAd.setId(TAGIMAGE);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            ivAd.setLayoutParams(imageParams);
            ivAd.setBackground(new BitmapDrawable(activity.getResources(),adBitmap));
            layout.addView(ivAd);
            ivAd.setOnTouchListener(CSJInterstitialHandlerImpl.this);

            //广告Logo
            ivLogo =new ImageView(activity);
            RelativeLayout.LayoutParams adLogoParams = new RelativeLayout.LayoutParams(24*displayWidth/1080, 24*displayWidth/1080);
            adLogoParams.addRule(RelativeLayout.ALIGN_BOTTOM,TAGIMAGE);
            adLogoParams.addRule(RelativeLayout.ALIGN_RIGHT,TAGIMAGE);
            ivLogo.setLayoutParams(adLogoParams);
            ivLogo.setAlpha(0.5f);
            layout.addView(ivLogo);

            //广告的TextView logo
            // logo的文字
            tvLogo=new TextView(activity);
            RelativeLayout.LayoutParams adLogoTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            adLogoTextParams.addRule(RelativeLayout.ALIGN_BOTTOM,TAGIMAGE);
            adLogoTextParams.addRule(RelativeLayout.ALIGN_RIGHT,TAGIMAGE);
            tvLogo.setLayoutParams(adLogoTextParams);
            tvLogo.setTextSize(10);
            tvLogo.setTextColor(Color.parseColor("#8C8C8C8C"));
            String sLogo = metaGroupBean.getAdMark();
            if (PublicUtils.isEmpty(sLogo)){
                tvLogo.setText("广告");
            }else{
                tvLogo.setText(sLogo);
            }
            layout.addView(tvLogo);

            if(logoBitmap!=null){
                PublicUtils.showImage(activity, ivLogo,logoBitmap);
            }else{
                tvLogo.setVisibility(GONE);
            }

            String adText = metaGroupBean.getAdTitle();
            if (!PublicUtils.isEmpty(adText)){
                tvSource = new TextView(activity);
                RelativeLayout.LayoutParams advertisingSourcepParams= new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                advertisingSourcepParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                advertisingSourcepParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                int left=60*displayWidth/1080;
                int right=60*displayWidth/1080;
                tvSource.setBackgroundColor(Color.parseColor("#99666666"));
                tvSource.setGravity(Gravity.CENTER_HORIZONTAL);
                tvSource.setPadding(left,5,right,5);
                tvSource.setLayoutParams(advertisingSourcepParams);
                tvSource.setTextSize(14);
                tvSource.setTextColor(Color.parseColor("#ffffff"));
                tvSource.setSingleLine(true);
                tvSource.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                tvSource.setText(adText);
                layout.addView(tvSource);
            }


            //关闭按钮的显示
            createCloseBtn();
            layout.addView(tvClose);
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = location.getFinalwidth();
            lp.height = location.getFinalHeight();
            dialogWindow.setAttributes(lp);

            if (apiOrsdkType==1){
                //广告展示上报
                interstitialAdListener.onAdShow();
                interstitialAdListener.onAdExposure();
                BeanUtils.track(metaGroupBean.getWinNoticeUrls(),activity,location);
            }else if (apiOrsdkType==2){//百度sdk信息流
            }else if(apiOrsdkType==3){//穿山甲sdk信息流
                touTiaoClick();
            }else if (apiOrsdkType==4){//广点通sdk信息流
                interstitialAdListener.onAdShow();
                nativeUnifiedADBean.onExposured(layout); // 需要先调用曝光接口
                interstitialAdListener.onAdExposure();
            }else if (apiOrsdkType==5){
                //绑定广告view事件交互
                bindViewInteraction(ttNativeAd);
            }

        }

        private void createCloseBtn() {
            tvClose = new TextView(activity);
            RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            closeParams.addRule(RelativeLayout.ALIGN_TOP,TAGIMAGE);
            closeParams.addRule(RelativeLayout.ALIGN_RIGHT,TAGIMAGE);
            tvClose.setLayoutParams(closeParams);
            tvClose.setBackgroundColor(Color.parseColor("#bb8C8C8C"));
            tvClose.setPadding(20,5,20,5);
            tvClose.setTextSize(18);
            tvClose.setText("×");
            tvClose.setTextColor(Color.parseColor("#FFFFFF"));
            if(!SdkHelper.isHit(dianjilv)){
                tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDialog();
                        if(!isSdk){
                            BeanUtils.track(arrSkipTrackUrl,activity,location);
                        }
                        dismiss();
                        interstitialAdListener.onAdDismissed();
                    }
                });
            }

        }

    }

    private void closeDialog() {
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }

        if (logoBitmap!=null&&!logoBitmap.isRecycled()){
            logoBitmap.recycle();
            logoBitmap=null;
        }
    }


    @Override
    public void notifyClickListener() {
        interstitialAdListener.onAdClicked();
    }

    @Override
    protected void setOnclick(View v) {
        if (NetworkHelper.isNetworkAvailable(activity)&&metaGroupBean!=null) {
            if (apiOrsdkType==1){
                onclick(metaGroupBean,ydtAdBeanList.get(0),source);
            }else if (apiOrsdkType==2){
            }else if (apiOrsdkType==3){
                //已处理
            }else if(apiOrsdkType==4){
                //已处理
                nativeUnifiedADBean.onClicked(v); // 点击接口
                LogManager.updatelog(adResponse.getClientRequest().getRequestId(),AdEventActions.ACTION_AD_CLICK,IReportService.Type.TYPE_SDK,AdType.INTERSTITIAL.getStringValue(),channelId,source);
            }

            if (dialog!=null&&dialog.isShowing()){
                dialog.dismiss();
                closeDialog();
            }
        }
    }

    /**
     * 跳转到落地页
     * */
    @Override
    protected void jumpToWebview() {
        Intent intent=new Intent(activity,WebviewActivity.class);
        intent.putExtra("mClickUrl",mClickUrl);
        String title=metaGroupBean.getAdTitle()==null?"":metaGroupBean.getAdTitle();
        intent.putExtra("title",title);
        activity.startActivity(intent);
    }

    public void adInterstitialSDKShow(){
        try {
            currentSize++;
            isSdk = true;
            if (fillType==2) {
                //原生填充
                if (source==DataSource.SDK_BAIDU){//百度
                } else if(source==DataSource.SDK_CSJ){//穿山甲
                    PublicUtils.initCSJAppId(activity,bean.getAppId(),bean.getAppName());
                    //step2:创建TTAdNative对象,用于调用广告请求接口，createAdNative(Context context) 插屏广告context需要传入Activity对象
                    mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
                    //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
                    TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
                    loadInterstitialAdWithCSJ(bean.getSlotId()); //数据方法填充
                }
            }else if (fillType==1){
                //信息流填充插屏
                xinxilMakeItem();
            }
        }catch (Exception e){
            onFailHandle(ErrorCode.SDKCSJ.ERROR_HANDLE,e.getMessage());
        }
    }

    private void xinxilMakeItem(){
        if (source==DataSource.SDK_GDT){//百度
        }else if (source==DataSource.SDK_GDT){//广点通
            Logger.e("source","广点通");
//            initNativeExpressAD();
        }else if(source==DataSource.SDK_CSJ){//穿山甲
            PublicUtils.initCSJAppId(activity,bean.getAppId(),bean.getAppName());
            //step1:初始化sdk
            TTAdManager ttAdManager = TTAdManagerHolder.get();
            //step2:创建TTAdNative对象,用于调用广告请求接口
            mTTAdNative = ttAdManager.createAdNative(activity);
            //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
            loadToutiaoListAd();
        }
    }

    /**
     * 加载feed广告 穿山甲
     */
    private void loadToutiaoListAd() {
        try {
            Logger.e("apirunnable","穿山甲信息流填插屏");
            //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
            if (bean.getHeight()<=0||bean.getWidth()<=0){
                bean.setHeight(388);
                bean.setWidth(690);
            }
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(bean.getSlotId())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(bean.getWidth(), bean.getHeight())
                    .setAdCount(1)
                    .build();
            //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
            mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int code, String message) {
                    onFailHandle(code,message);
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> ads) {
                    if (ads == null || ads.isEmpty()) {
                        onFailHandle(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY,ErrorMessage.Ad.ERROR_GET_ADS);
                        return;
                    }
                    TTFeedAd ttFeedAd=ads.get(0);
                    ad=ttFeedAd;
                    YdtAdBean ydtAdBean=new YdtAdBean();
                    // ydtAdBean.setAdlogo(ads.get(i).getAdLogo());
                    List<YdtAdBean.MetaGroupBean> metaGroupBeans=new ArrayList<>();
                    YdtAdBean.MetaGroupBean metaGroupBean=new YdtAdBean.MetaGroupBean();
                    metaGroupBean.setAdTitle(ttFeedAd.getTitle());
                    List<String> imageList=new ArrayList<>();
                    for (int k=0;k<ttFeedAd.getImageList().size();k++){
                        imageList.add(ttFeedAd.getImageList().get(k).getImageUrl());
                    }
                    metaGroupBean.setImageUrl(imageList);
                    metaGroupBean.setAppSize(ttFeedAd.getAppSize());
                    List<String> descList=new ArrayList<>();
                    descList.add(ttFeedAd.getDescription());
                    metaGroupBean.setDescs(descList);
                    List<String> iconList=new ArrayList<>();
                    iconList.add(ttFeedAd.getIcon().getImageUrl());
                    metaGroupBean.setIconUrls(iconList);
                    metaGroupBean.setInteractionType(ttFeedAd.getInteractionType());
                    metaGroupBeans.add(metaGroupBean);
                    ydtAdBean.setMetaGroup(metaGroupBeans);
                    apiOrsdkType=3;
                    setInformationView(ydtAdBean);
                }
            });
        }catch (Exception e){
            onFailHandle(ErrorCode.SDKCSJ.ERROR_HANDLE,e.getMessage());
        }
    }


    private void setInformationView(final YdtAdBean ydtAdBean){
        isDowload=false;
        metaGroupBean=ydtAdBean.getMetaGroup().get(0);
        view_id=ydtAdBean.getView_id();
        mClickUrl=metaGroupBean.getClickUrl();
        arrSkipTrackUrl=metaGroupBean.getArrSkipTrackUrl();
        if (!PublicUtils.isEmpty(metaGroupBean.getBrandName())){
            apkName=metaGroupBean.getBrandName();
        }else if (!PublicUtils.isEmpty(metaGroupBean.getAdTitle())){
            apkName=metaGroupBean.getAdTitle();
        }else{
            apkName="apk";
        }
        getAdLogo(ydtAdBean.getAdlogo());
        List<String>  imgs = metaGroupBean.getImageUrl();
        if(imgs!=null&&imgs.size()>0){
            String adImage = imgs.get(0);
            if(TextUtils.isEmpty(adImage)) {
                handleErrror(ErrorCode.SDKCSJ.ERROR_NO_AD,ErrorMessage.Ad.ERROR_IMAGE_NOT_FOUND);
            } else {
                getBitmap(adImage);
            }
        }
    }

    private void loadInterstitialAdWithCSJ(String codeId) {
        try {
            //step4:创建插屏广告请求参数AdSlot,具体参数含义参考文档
            if (bean.getHeight()<=0||bean.getWidth()<=0){
                bean.setHeight(600);
                bean.setWidth(600);
            }
            AdSlot adSlot = new Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(bean.getWidth(), bean.getHeight()) //根据广告平台选择的尺寸，传入同比例尺寸
                    .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)//请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                    .setAdCount(1)
                    .build();
            //step5:请求广告，调用插屏广告异步请求接口
            mTTAdNative.loadNativeAd(adSlot,  new TTAdNative.NativeAdListener() {
                @Override
                public void onError(int code, String message) {
                    Logger.e(TAG,"onError enter , code = " + code + " , message = " + message);
                    onFailHandle(code,ErrorMessage.Ad.ERROR_GET_ADS+"(src="+DataSource.SDK_CSJ +")");
                }

                @Override
                public void onNativeAdLoad(List<TTNativeAd> ads) {
                    if (ads == null || ads.get(0) == null) {
                        Logger.i(TAG,"onNativeAdLoad data is empty");
                        onFailHandle(ErrorCode.SDKCSJ.ERROR_RESPONSE_DATA_EMPTY,ErrorMessage.Ad.ERROR_DATA_EMPTY);
                        return;
                    }
                    TTNativeAd ttNativeAds=ads.get(0);
                    ttNativeAd=ttNativeAds;
                    YdtAdBean ydtAdBean=new YdtAdBean();
                    // ydtAdBean.setAdlogo(ads.get(i).getAdLogo());
                    List<YdtAdBean.MetaGroupBean> metaGroupBeans=new ArrayList<>();
                    YdtAdBean.MetaGroupBean metaGroupBean=new YdtAdBean.MetaGroupBean();
                    metaGroupBean.setAdTitle(ttNativeAds.getTitle());
                    List<String> imageList=new ArrayList<>();
                    for (int k=0;k<ttNativeAds.getImageList().size();k++){
                        imageList.add(ttNativeAds.getImageList().get(k).getImageUrl());
                    }
                    metaGroupBean.setImageUrl(imageList);
                    metaGroupBean.setAppSize(ttNativeAds.getAppSize());
                    List<String> descList=new ArrayList<>();
                    descList.add(ttNativeAds.getDescription());
                    metaGroupBean.setDescs(descList);
                    List<String> iconList=new ArrayList<>();
                    iconList.add(ttNativeAds.getIcon().getImageUrl());
                    metaGroupBean.setIconUrls(iconList);
                    metaGroupBean.setInteractionType(ttNativeAds.getInteractionType());
                    metaGroupBeans.add(metaGroupBean);
                    ydtAdBean.setMetaGroup(metaGroupBeans);
                    apiOrsdkType = 5;
                    setInformationView(ydtAdBean);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            onFailHandle(ErrorCode.SDKCSJ.ERROR_HANDLE,ErrorMessage.Ad.ERROR_HANDLE);
        }
    }


    private void bindViewInteraction(TTNativeAd ad) {
        Logger.i(TAG,"bindViewInteraction enter");
        //可以被点击的view, 比如标题、icon等,点击后尝试打开落地页，也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(ivAd);

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction(layout, clickViewList, null, null, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                Logger.i(TAG,"bindViewInteraction onAdClicked enter");
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                    closeDialog();
                    interstitialAdListener.onAdClicked();
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                Logger.i(TAG,"bindViewInteraction onAdCreativeClick enter");
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                    closeDialog();
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    interstitialAdListener.onAdShow();
                }
            }
        });

    }

    private void onFailHandle(int code,String message){
        if (size >= 2 && currentSize == 1){
            bean = adModel.getParams().get(1);
            fillType = bean.getSlotFill();
            source=bean.getSource();
            adInterstitialSDKShow();
        }else{
            handleErrror(code,message);
        }
    }

    public void destory(){
        closeDialog();
    }
}
