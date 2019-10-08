package com.analytics.sdk.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.network.TrafficTracker;
import com.analytics.sdk.common.runtime.activity.ActivityTaskManager;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdFillType;
import com.analytics.sdk.view.strategy.os.InstrumentationHack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public final class SdkHelper {

    static final String TAG = "SdkHelper";

    public static final String NEXT_REQUEST = "next_request";

    public static AdRequest clone(AdRequest adRequest){
        return new AdRequest.Builder(adRequest).build();
    }

    public static AdRequest buildNextRequest(AdRequest clientRequest) {
        return new AdRequest.Builder(clientRequest).appendParameter(NEXT_REQUEST,true).build();
    }

    public static void appendPoint(AdRequest adRequest,PointF point){
        if(hasPoint(adRequest)){
            removePoint(adRequest);
        }
        adRequest.getExtParameters().putParcelable("point",point);
    }

    public static boolean hasPoint(AdRequest adRequest){
        return adRequest.getExtParameters().containsKey("point");
    }

    public static void removePoint(AdRequest adRequest){
        adRequest.getExtParameters().remove("point");
    }

    public static PointF getPoint(AdRequest adRequest){
        return (PointF)adRequest.getExtParameters().getParcelable("point");
    }

    public static boolean isNextRequest(AdRequest adRequest){
        Bundle bundle = adRequest.getExtParameters();
        if(bundle.containsKey(NEXT_REQUEST)){
            boolean tryNextReuqest = bundle.getBoolean(NEXT_REQUEST);
            return tryNextReuqest;
        }
        return false;
    }

    /**
     * 是否命中点击率
     * use StrategyHelper#isHitBlack
     */
    @Deprecated
    public static boolean isHit(float random){
        return StrategyHelper.isHit(random);
    }

    public static String getCacheKeyWithRequestCodeId(AdRequest adRequest){
        int sdkVersion = AdConfig.getDefault().getSdkVersion();
        String cacheKey = sdkVersion+"_"+adRequest.getCodeId();
        return cacheKey;
    }

    public static int getRandom(int min,int max){
        Random random = new Random();
        int result = random.nextInt(max)%(max-min+1) + min;
        return result;
    }

    public static final String getSourceName(int source){
        if(source == DataSource.API_GDT) {
            return "API GDT";
        } else if(source == DataSource.SDK_BAIDU) {
            return "SDK BAIDU";
        } else if(source == DataSource.SDK_GDT) {
            return "SDK GDT";
        } else if(source == DataSource.SDK_CSJ) {
            return "SDK CSJ";
        } else {
            return "UNKNOW";
        }
    }

    public static String getMotionEventActionString(MotionEvent motionEvent){
        int action = motionEvent.getAction();
        if(MotionEvent.ACTION_DOWN == action) {
            return "down";
        } else if(MotionEvent.ACTION_UP == action) {
            return "up";
        } else if(MotionEvent.ACTION_MOVE == action) {
            return "move";
        } else if(MotionEvent.ACTION_CANCEL == action) {
            return "cancel";
        } else {
            return "unknow";
        }
    }

    public static String getFillTypeName(int fillType) {
        if(fillType == AdFillType.TEMPLATE.intValue()){
            return AdFillType.TEMPLATE.name();
        } else if(fillType == 1){
            return "INFORMATION";
        } else {
            return "UNKNOW";
        }
    }

//    public static String getBannerFillTypeName(int fillType){
//        if(fillType == CSJBannerHandler2.TYPE_VIEW_TEMPLATE){
//            return "TEMPLATE";
//        } else {
//            return "NATIVE";
//        }
//    }
//
//    public static String getInterstitialTypeName(int fillType){
//        if(fillType == TableScreenActivity.TYPE_NATIVE){
//            return "NATIVE"; //原生填充
//        } else if(fillType == TableScreenActivity.TYPE_INFORMATION){
//            return "INFORMATION"; //信息流填充
//        } else {
//            return "UNKNOW";
//        }
//    }

    /**
     * 表示缩进
     */
    static final int JSON_INDENT = 4;

    public static String format(Object object) {
        if(object == null){
            return "";
        }
        String message = String.valueOf(object);
        if(TextUtils.isEmpty(message)){
            return "";
        }
        try {
            message = message.trim();
            if (message.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(message);
                message = jsonObject.toString(JSON_INDENT);
            }
            if (message.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(message);
                message = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static String buildErrorMessage(ViewGroup clientAdContainer, ViewGroup strategyRootLayout, AdRequest adRequest, int errorCode, String errorMessage, long startRequestAdTime, TrafficTracker.TrackResult trackResult){

        try {

            final long buildEnterTime = System.currentTimeMillis();

            Activity activity = adRequest.getActivity();

            int clientAdContainerVisibility = clientAdContainer.getVisibility();
            boolean clientAdContainerShown = clientAdContainer.isShown();
            int clientAdContainerWindowVisibility = clientAdContainer.getWindowVisibility();
            int clientAdContainerWidth = clientAdContainer.getWidth();
            int clientAdContainerHeight = clientAdContainer.getHeight();

            int clientAdContainerParentWidth = 0;
            int clientAdContainerParentHeight = 0;
            boolean clientAdContainerParentShown = false;

            if(clientAdContainer.getParent() != null){
                ViewGroup viewGroup = (ViewGroup)clientAdContainer.getParent();
                clientAdContainerParentWidth = viewGroup.getWidth();
                clientAdContainerParentHeight = viewGroup.getHeight();
                clientAdContainerParentShown = viewGroup.isShown();
            }

            int strategyAdContainerVisibility = strategyRootLayout.getVisibility();
            boolean strategyAdContainerShown = strategyRootLayout.isShown();
            int strategyAdContainerWindowVisibility = strategyRootLayout.getWindowVisibility();
            int strategyAdContainerWidth = strategyRootLayout.getWidth();
            int strategyAdContainerHeight = strategyRootLayout.getHeight();

            boolean isNetworkConnected = NetworkHelper.isNetworkAvailable(AdClientContext.getClientContext());
            boolean isWifiAvailable = NetworkHelper.isWifiAvailable(AdClientContext.getClientContext());
            int wifiLevel = 0;
            if(isWifiAvailable){
                wifiLevel = NetworkHelper.getWifiLevel(AdClientContext.getClientContext());
            }

            long splash2Request = startRequestAdTime - InstrumentationHack.splashActivityState.onCreateTime;
            long splashUsedTime = buildEnterTime - InstrumentationHack.splashActivityState.onCreateTime;

            int mResumed = InstrumentationHack.splashActivityState.mResumed;
            int mStopped = InstrumentationHack.splashActivityState.mStopped;
            int mFinished= InstrumentationHack.splashActivityState.mFinished;
            int mDestroyed= InstrumentationHack.splashActivityState.mDestroyed;

            Activity topActivity = ActivityTaskManager.getInstance().peekTopActivity();
            String topActivityName = topActivity != null ? topActivity.getClass().getSimpleName() : "";
            boolean topActivityDestoryed = topActivity != null ? UIHelper.isActivityDestoryed(topActivity) : false;

            StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder
                    .append("sys:").append(AdClientContext.displayWidth +":"+AdClientContext.displayHeight+":"+UIHelper.getDenstiyDpi(AdClientContext.getClientContext())).append(",")
                    .append("clt_v:").append(clientAdContainerVisibility+":"+String.valueOf(clientAdContainerShown)+":"+clientAdContainerWindowVisibility+":"+clientAdContainerWidth+":"+clientAdContainerHeight).append(",")
                    .append("clt_p:").append(clientAdContainerParentWidth+":"+clientAdContainerParentHeight+":"+(String.valueOf(clientAdContainerParentShown))).append(",")
                    .append("scn_ori:").append(UIHelper.getScreenOrientationString(AdClientContext.getClientContext())).append(",")
                    .append("stgy_v:").append(strategyAdContainerVisibility+":"+String.valueOf(strategyAdContainerShown)+":"+strategyAdContainerWindowVisibility+":"+strategyAdContainerWidth+":"+strategyAdContainerHeight).append(",")
                    .append("act:").append(UIHelper.isActivityDestoryed(activity)+":"+mResumed+":"+mStopped+":"+mFinished+":"+mDestroyed+":"+topActivityName+":"+topActivityDestoryed).append(",")
                    .append("req_tm:").append(buildEnterTime - startRequestAdTime).append(",")
                    .append("iit_tm:").append(buildEnterTime - AdClientContext.getInitTime()).append(",")
                    .append("splh_t_req_tm:").append(splash2Request).append(",")
                    .append("splh_t_err_tm:").append(splashUsedTime).append(",")
                    .append("tfc:").append(trackResult.currentBandwidthQuality+":"+trackResult.downloadKBitsPerSecond).append(",")
                    .append("net:").append(isNetworkConnected+":"+isWifiAvailable+":"+wifiLevel+":"+NetworkHelper.getCarrier(activity)).append(",")
                    .append("gt_net:").append(NetworkHelper.getDataNet(activity)+":"+NetworkHelper.getPhoneNet(activity)).append(",")
                    .append("3rd:").append(errorCode+"["+errorMessage+"]");

            return errorMessageBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            return "buildErrorMessage Exception";
        }

    }

    public static boolean isSupportInit(Context context){

        if("com.meizu.media.ebook".equals(context.getPackageName())){
            return false;
        }
        return true;
    }

}
