package com.analytics.sdk.service.report;


import android.os.Build;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by kf on 2018/9/19.
 */
// FIXME: 2019/6/17 需要移动到ReportService中
public class LogManager {

    static final String TAG = "LogManager";

    public static boolean log = true;

    /**
     * 广告上报服务器
     * @param requestId 唯一ID
     * @param action 上报的类型，例如：曝光，点击、安装、下载等
     * @param category  分类，API还是SDK广告
     * @param adType 广告类型，信息流、开屏、插屏、激励视频
     * @param channelId 代码位ID
     * @param source 广告源
     */
    public static void updatelog(String requestId,String action, String category, String adType, String channelId, int source){
        Logger.i(TAG,"uploadLog enter, isSendLog = " + log);
        if (log){
            sendTrackContent(requestId,action,category, adType,channelId,source,ErrorCode.NONE);
        }

    }

//    public static void updatelog(String action, String category, String adType, String channelId, int source){
//        Logger.i(TAG,"uploadLog enter, isSendLog = " + log);
//        if (log){
//            sendTrackContent("",action,category, adType,channelId,source,ErrorCode.NONE);
//        }
//
//    }

    public static void updatelog(String action, String category, String adType, String channelId, int source, int errorCode){
        Logger.i(TAG,"uploadLog enter, isSendLog = " + log);
        if (log){
            sendTrackContent("",action,category, adType,channelId,source,errorCode);
        }

    }

    private static JSONObject makeSimpleParams(String requestId,String action, String category, String adType, String channelId, int source, int errorCode) throws JSONException {
        JSONObject trackJson = new JSONObject();
        trackJson.put("requestId",requestId);
        trackJson.put("category",category);
        trackJson.put("action",action);
        trackJson.put("adType",adType);
        trackJson.put("apiOrSdkAdType",source);
        trackJson.put("time",System.currentTimeMillis());
        trackJson.put("channel", channelId);
        trackJson.put("version",AdConfig.getDefault().getSdkVersion());
        trackJson.put("errorCode",errorCode);

        return trackJson;
    }

    private static void sendTrackContent(final String requestId,final String action, String category, String adType, final String channelId, int apiOrSdkAdType, int errorCode) {
        JSONObject logParams = null;
        try {
            logParams = makeSimpleParams(requestId,action,category,adType,channelId,apiOrSdkAdType,errorCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(logParams!=null){
            try{
                logParams.put("deviceId", DeviceHelper.getDeviceId(AdClientContext.getClientContext()));
                logParams.put("pkgName", AdClientContext.getClientContext().getPackageName());
                //todo:imei
                logParams.put("app_version",AppHelper.getVersionName(AdClientContext.getClientContext()));
                logParams.put("imei",DeviceHelper.getImei(AdClientContext.getClientContext()));
                logParams.put("phone_brand",Build.BRAND);
                logParams.put("phone_model",Build.MODEL);
                // 设置操作系统版本
                String osVersion=Build.VERSION.RELEASE;
                if (osVersion.length()==1){
                    osVersion=osVersion+".0.0";
                }
                if (osVersion.length()==3){
                    osVersion=osVersion+".0";
                }
                logParams.put("os_version",osVersion);
                int networkType = NetworkHelper.getNetworkState(AdClientContext.getClientContext());
                logParams.put("network_type",NetworkHelper.getNetworkTypeString(networkType));
            }catch (Exception e){
                e.printStackTrace();
            }
            final String params = logParams.toString();

            Logger.i(TAG,params);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //TODO:超时全局配置
                    String result = BufferedReaderUtils.getData(AdConfig.getDefault().getServerEnvConfig().getLogUrlV2(), params, 5000);
                    Logger.i(TAG,"sendTrackContent completed("+channelId+"_"+action+") result = " + result);
                }
            }).start();

        } else {
            Logger.i(TAG,"logParams is null");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = BufferedReaderUtils.getData(AdConfig.getDefault().getServerEnvConfig().getLogUrlV2() , "params is null", 5000);
                    Logger.i(TAG,"logParams is null("+channelId+"_"+action+") result = " + result);
                }
            }).start();
        }


    }
}
