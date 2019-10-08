package com.analytics.sdk.dynamic.a;

import android.text.TextUtils;
import android.view.MotionEvent;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.dynamic.common.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public final class SdkHelper {

    static final String TAG = "SdkHelper";

    /**
     * 是否命中点击率
     * @param random
     * @return
     */
    public static boolean isHit(float random){

        int defaultRandom = getDefaultRandom();
        boolean isDebugClickStrategy = false;
        int serverRandom = isDebugClickStrategy ? 100 : convert2IntRandom(random);

        Logger.i(TAG,"isHitBlack enter , defaultRandom = " + defaultRandom + " , serverRandom = " + serverRandom + " , serverFloatRandom = " + random);

        return (defaultRandom < serverRandom);
    }


    public static int convert2IntRandom(float random){
        return (int) (random * 100);
    }

    public static int getDefaultRandom(){
        return getRandom(0,99);
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
        if(fillType == 2){
            return "TEMPLATE";
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

}
