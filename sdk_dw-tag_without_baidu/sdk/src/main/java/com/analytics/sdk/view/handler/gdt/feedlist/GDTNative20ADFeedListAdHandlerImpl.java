package com.analytics.sdk.view.handler.gdt.feedlist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.LayoutStyle;
import com.analytics.sdk.client.ViewStyle;
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
import com.analytics.sdk.view.handler.common.FeedListLayoutStyle;
import com.analytics.sdk.view.strategy.crack.ProxyActivity;
import com.analytics.sdk.view.strategy.crack.ProxyContext;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;

import java.util.ArrayList;
import java.util.List;

/**
 * 广点通信息流，
 * 自渲染2.0的实现
 */
public class GDTNative20ADFeedListAdHandlerImpl extends BasicAdHandler {

    static final String TAG = GDTNative20ADFeedListAdHandlerImpl.class.getSimpleName();
    private Activity activity;
    private List<AdView> feedlistAdViews = new ArrayList<>();

    @Override
    protected EventActionList buildEventActionList() {
        return AdEventActions.BASE_HANDLER.clone().addActionList(AdEventActions.BASE_FEEDLIST);
    }


    @Override
    protected void onHandleAd(final AdResponse adResponse, AdListeneable clientAdListener, final ConfigBeans configBeans) throws AdSdkException {
        Logger.i(TAG,"handleAd enter , " + adResponse.getClientRequest());
        Logger.i(TAG,"handleAd enter , configBeans " + configBeans);

        this.activity = adResponse.getClientRequest().getActivity();

        final String appid = configBeans.getAppId();
        final String slotid = configBeans.getSlotId();

//        final String appid = "1109723879"; //configBeans.getAppId()
//        final String slotid = "2050987275403426"; //configBeans.getSlotId()
//
//        ProxyActivity.PROXY_PACKAGE_NAME = "com.gstarmc.android";
//
//        final ProxyContext proxyContext = new ProxyContext(activity.getBaseContext(),0);
//        try {
//            Field field = ContextWrapper.class.getDeclaredField("mBase");
//            field.setAccessible(true);
//            field.set(activity,proxyContext);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        NativeUnifiedAD mAdNativeUnifiedAD = new NativeUnifiedAD(activity, appid, slotid, new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {

                if(ads == null || ads.size() == 0){
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,new AdError(ErrorCode.FeedList.ERROR_VIDEO_SHOWN,"数据为空")));
                    return;
                }

                int adsSize = ads.size();

                Logger.i(TAG,"onADLoaded enter , ads size = " + adsSize);

                for(int i = 0;i < adsSize;i++){
                    NativeUnifiedADData ad = ads.get(i);

                    int adViewStyle = configBeans.getXxlStyle();

                    Logger.i(TAG,"adViewStyle = " + adViewStyle);

                    if(adViewStyle == FeedListLayoutStyle.BIG.intValue()){ //大图

                        AdView view = buildBigAdView(ad);
                        feedlistAdViews.add(view);

                    } else if(adViewStyle == FeedListLayoutStyle.LEFT_IMAGE.intValue()){ //左图右文

                        AdView view = buildLeftImageAdView(ad);
                        feedlistAdViews.add(view);

                    } else if(adViewStyle == FeedListLayoutStyle.RIGHT_IMAGE.intValue()) { //左文右图

                        AdView view = buildRightImageAdView(ad);
                        feedlistAdViews.add(view);

                    } else if(adViewStyle == FeedListLayoutStyle.THREE_IMAGE.intValue()) { //三图

                        AdView view = buildThreeImageAdView(ad);
//                        if(view == null){
//                            view = buildLeftImageAdView(ad);
//                        }
                        if(view != null){
                            feedlistAdViews.add(view);
                        }

                    } else {
                        AdView view = buildBigAdView(ad);
                        feedlistAdViews.add(view);
                    }

                }

                EventScheduler.dispatch(Event.obtain(AdEventActions.FeedList.ACTION_AD_LOADED,adResponse.setResponseFeedlistCount(adsSize),feedlistAdViews));
            }


            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {

                AdError newAdError = new AdError(adError.getErrorCode(),adError.getErrorMsg());
                Logger.i(TAG,"onNoAD enter , " + newAdError);

                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR,adResponse,newAdError));
            }
        });
        mAdNativeUnifiedAD.loadData(adResponse.getClientRequest().getAdRequestCount());
    }

    /**
     * 三图
     */
    private AdView buildThreeImageAdView(NativeUnifiedADData ad) {

        LinearLayout strategyRootLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_feedlist_gdt_native20_threeimage_ad,null);
        int patternType = ad.getAdPatternType();
        Logger.i(TAG,"renderAdUi enter , patternType = " + patternType);
        if (patternType == AdPatternType.NATIVE_3IMAGE) {

            final GDTNativeAdViewImpl gdtNativeAdView = new GDTNativeAdViewImpl(strategyRootLayout,adResponse,ad);
            NativeAdContainer nativeAdContainer = strategyRootLayout.findViewById(R.id.native_ad_container);

            ImageView adImage1 = nativeAdContainer.findViewById(R.id.img_1);
            ImageView adImage2 = nativeAdContainer.findViewById(R.id.img_2);
            ImageView adImage3 = nativeAdContainer.findViewById(R.id.img_3);

            final TextView adTitle = nativeAdContainer.findViewById(R.id.ad_title);
            final TextView adClose = strategyRootLayout.findViewById(R.id.ad_close);

            applyStyle(strategyRootLayout);
            List<View> clickableViews = new ArrayList<>();

            clickableViews.add(adTitle);
            clickableViews.add(adImage1);
            clickableViews.add(adImage2);
            clickableViews.add(adImage3);

            ad.bindAdToView(activity, nativeAdContainer, null, clickableViews);

            if(adClose.getVisibility() == View.VISIBLE){
                adClose.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse,gdtNativeAdView));
                    }
                });
            }

            final AQuery aquery = new AQuery(strategyRootLayout.findViewById(R.id.root));

            List<String> imageUrls = ad.getImgList();

            String imageUlr = ad.getImgUrl();

            Logger.i(TAG,"renderAdUi enter , NATIVE_3IMAGE , imageUlr = " + imageUlr);
            Logger.i(TAG,"renderAdUi enter , NATIVE_3IMAGE , imageUrl size = " + imageUrls.size());

            if(imageUrls.size() == 0){
                return null;
            }

            String title = ad.getTitle();

            aquery.id(R.id.img_1).image(imageUrls.get(0), false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });

            aquery.id(R.id.img_2).image(imageUrls.get(1), false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });

            aquery.id(R.id.img_3).image(imageUrls.get(2), false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });

            aquery.id(R.id.ad_title).text(title);

            ad.setNativeAdEventListener(new NativeADEventListener(){

                @Override
                public void onADExposed() {
                    Logger.i(TAG,"onADExposed enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse,gdtNativeAdView));
                }

                @Override
                public void onADClicked() {
                    Logger.i(TAG,"onADClicked enter");
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse,gdtNativeAdView));
                }

                @Override
                public void onADError(com.qq.e.comm.util.AdError adError) {
                    Logger.i(TAG,"onADError enter , " + adError.getErrorMsg() + " , code = " + adError.getErrorCode());
                }

                @Override
                public void onADStatusChanged() {
                    Logger.i(TAG,"onADStatusChanged enter");
                }
            });

            return gdtNativeAdView;

        }

        return null;

    }

    /**
     * 左文右图
     */
    private AdView buildRightImageAdView(NativeUnifiedADData ad) {
        LinearLayout strategyRootLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_feedlist_gdt_native20_rightimage_ad,null);
        return getLeftOrRightAdView(ad, strategyRootLayout);
    }

    /**
     * 左图右文
     */
    private AdView buildLeftImageAdView(NativeUnifiedADData ad) {
        LinearLayout strategyRootLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_feedlist_gdt_native20_leftimage_ad,null);
        return getLeftOrRightAdView(ad, strategyRootLayout);
    }

    @NonNull
    private AdView getLeftOrRightAdView(NativeUnifiedADData ad, LinearLayout strategyRootLayout) {
        final GDTNativeAdViewImpl gdtNativeAdView = new GDTNativeAdViewImpl(strategyRootLayout,adResponse,ad);
        NativeAdContainer nativeAdContainer = strategyRootLayout.findViewById(R.id.native_ad_container);

        ImageView adImage = nativeAdContainer.findViewById(R.id.ad_image);
        final TextView adTitle = nativeAdContainer.findViewById(R.id.ad_title);
        final TextView adDesc = strategyRootLayout.findViewById(R.id.ad_desc);
        final TextView adClose = strategyRootLayout.findViewById(R.id.ad_close);

        applyStyle(strategyRootLayout);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adImage);
        clickableViews.add(adTitle);
        clickableViews.add(adDesc);

        ad.bindAdToView(activity, nativeAdContainer, null, clickableViews);

        if(adClose.getVisibility() == View.VISIBLE){
            adClose.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse,gdtNativeAdView));
                }
            });
        }

        final AQuery aquery = new AQuery(strategyRootLayout.findViewById(R.id.root));

        int patternType = ad.getAdPatternType();
        Logger.i(TAG,"renderAdUi enter , patternType = " + patternType);
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT || patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            Logger.i(TAG,"renderAdUi enter , NATIVE_2IMAGE_2TEXT");

            String imageUrl = ad.getImgUrl();

            String title = ad.getTitle();
            String desc = ad.getDesc();

            aquery.id(R.id.ad_image).image(imageUrl, false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });
            aquery.id(R.id.ad_title).text(title);
            aquery.id(R.id.ad_desc).text(desc);

        }

        ad.setNativeAdEventListener(new NativeADEventListener(){

            @Override
            public void onADExposed() {
                Logger.i(TAG,"onADExposed enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse,gdtNativeAdView));
            }

            @Override
            public void onADClicked() {
                Logger.i(TAG,"onADClicked enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse,gdtNativeAdView));
            }

            @Override
            public void onADError(com.qq.e.comm.util.AdError adError) {
                Logger.i(TAG,"onADError enter , " + adError.getErrorMsg() + " , code = " + adError.getErrorCode());
            }

            @Override
            public void onADStatusChanged() {
                Logger.i(TAG,"onADStatusChanged enter");
            }
        });

        return gdtNativeAdView;
    }

    /**
     * 大图
     */
    private AdView buildBigAdView(final NativeUnifiedADData ad) {

        LinearLayout strategyRootLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_feedlist_gdt_native20_big_ad,null);

        final GDTNativeAdViewImpl gdtNativeAdView = new GDTNativeAdViewImpl(strategyRootLayout,adResponse,ad);
        NativeAdContainer nativeAdContainer = strategyRootLayout.findViewById(R.id.native_ad_container);

        ImageView adImage = nativeAdContainer.findViewById(R.id.ad_image);
        final TextView adTitle = nativeAdContainer.findViewById(R.id.ad_title);
        final TextView adDesc = strategyRootLayout.findViewById(R.id.ad_desc);
        final TextView adClose = strategyRootLayout.findViewById(R.id.ad_close);

        applyStyle(strategyRootLayout);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(adImage);
        clickableViews.add(adTitle);
        clickableViews.add(adDesc);

        ad.bindAdToView(activity, nativeAdContainer, null, clickableViews);

        if(adClose.getVisibility() == View.VISIBLE){
            adClose.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS,adResponse,gdtNativeAdView));
                }
            });
        }

        final AQuery aquery = new AQuery(strategyRootLayout.findViewById(R.id.root));

        int patternType = ad.getAdPatternType();
        Logger.i(TAG,"renderAdUi enter , patternType = " + patternType);
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT || patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            Logger.i(TAG,"renderAdUi enter , NATIVE_2IMAGE_2TEXT");

            String imageUrl = ad.getImgUrl();

            String title = ad.getTitle();
            String desc = ad.getDesc();

            aquery.id(R.id.ad_image).image(imageUrl, false, true, 0, 0,
                    new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            if (iv.getVisibility() == View.VISIBLE) {
                                iv.setImageBitmap(bm);
                            }
                        }
                    });
//            aquery.id(R.id.ad_image).image(imageUrl, false, true, 0, 0,null,0);
//            aquery.id(R.id.ad_image).image(imageUrl, false, true, 0, 0,null,0,3.0f/10.0f);
//            aquery.id(R.id.ad_image).image(imageUrl, false, true, 0, 0,null,0,5.0f/6.0f);
            aquery.id(R.id.ad_title).text(title);
            aquery.id(R.id.ad_desc).text(desc);

        } else if ( patternType == AdPatternType.NATIVE_VIDEO) {
            Logger.i(TAG,"renderAdUi enter , NATIVE_VIDEO");
        }else if (patternType == AdPatternType.NATIVE_3IMAGE) {
            Logger.i(TAG,"renderAdUi enter , NATIVE_3IMAGE");
        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            Logger.i(TAG,"renderAdUi enter , NATIVE_3IMAGE");
        }

        ad.setNativeAdEventListener(new NativeADEventListener(){

            @Override
            public void onADExposed() {
                Logger.i(TAG,"onADExposed enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE,adResponse,gdtNativeAdView));
            }

            @Override
            public void onADClicked() {
                Logger.i(TAG,"onADClicked enter");
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK,adResponse,gdtNativeAdView));
            }

            @Override
            public void onADError(com.qq.e.comm.util.AdError adError) {
                Logger.i(TAG,"onADError enter , " + adError.getErrorMsg() + " , code = " + adError.getErrorCode());
            }

            @Override
            public void onADStatusChanged() {
                Logger.i(TAG,"onADStatusChanged enter");
            }
        });

        return gdtNativeAdView;
    }

    private void applyStyle(ViewGroup viewGroup) {

        final TextView adTitle = viewGroup.findViewById(R.id.ad_title);
        final TextView adDesc = viewGroup.findViewById(R.id.ad_desc);
        final TextView adClose = viewGroup.findViewById(R.id.ad_close);

        LayoutStyle layoutStyle = adRequest.getLayoutStyle();

        if(layoutStyle != null && !layoutStyle.isEmpty()) {
            ViewStyle titleStyle = layoutStyle.getViewStyle(ViewStyle.STYLE_TITLE);
            ViewStyle descStyle = layoutStyle.getViewStyle(ViewStyle.STYLE_DESC);

            applyTextViewStyle(adDesc,descStyle);
            applyTextViewStyle(adTitle,titleStyle);

            if(layoutStyle.isHiddenClose()){
                adClose.setVisibility(View.GONE);
            }

            if(layoutStyle.hasBgColor()){
                viewGroup.setBackgroundColor(layoutStyle.getBgColor());
            }

        }
    }

    private void applyTextViewStyle(TextView textView,ViewStyle viewStyle) {
        if(viewStyle != null && textView != null){
            if(viewStyle.hasTextSize()) {
                textView.setTextSize(viewStyle.getTextSize());
            }
            if(viewStyle.hasTextColor()){
                textView.setTextColor(viewStyle.getTextColor());
            }
            if(viewStyle.hasBgColor()) {
                textView.setBackgroundColor(viewStyle.getBgColor());
            }
        }
    }

    @Override
    public boolean recycle() {
        //destory();

        if(feedlistAdViews!=null){
            for(int i = 0;i < feedlistAdViews.size();i++){
                AdView adView = feedlistAdViews.get(i);
                adView.recycle();
            }

            feedlistAdViews.clear();
        }

        return true;
    }
}
