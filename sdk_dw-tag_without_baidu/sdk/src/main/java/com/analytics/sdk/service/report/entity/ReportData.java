package com.analytics.sdk.service.report.entity;

import android.graphics.PointF;
import android.os.Build;
import android.text.TextUtils;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.IReportServiceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ReportData {

    private String action;
    /**
     * SDK类型还是API类型
     */
    private String type;
    private AdError adError = AdError.EMPTY;
    private AdResponse adResponse;
    private String reportId;
    private JSONObject appendParams = new JSONObject();

    private ReportData(){}

    public static ReportData obtain(String message,String action){
        return obtain(new AdError(ErrorCode.NONE,message),action,"",null);
    }

    public static ReportData obtain(int errorCode,String errorMessage,String action){
        return obtain(new AdError(errorCode,errorMessage),action,"",null);
    }

    public static ReportData obtain(String action){
        return obtain(AdError.EMPTY,action,"",null);
    }

    public static ReportData obtain(AdError adError,String action){
        return obtain(adError,action,"",null);
    }

    public static ReportData obtain(String action,AdResponse adResponse){
        return obtain(AdError.EMPTY,action,TextUtils.isEmpty(adResponse.getReportType()) ? "" : adResponse.getReportType(),adResponse);
    }

    public static ReportData obtain(AdError adError, String action, AdResponse adResponse){
        return obtain(adError,action,TextUtils.isEmpty(adResponse.getReportType()) ? "" : adResponse.getReportType(),adResponse);
    }

    public static ReportData obtain(AdError adError,String action,String type,AdResponse adResponse){
        ReportData reportData = new ReportData();
        if(adResponse != null){
            reportData.reportId = adResponse.getClientRequest().getRequestId();
        } else {
            reportData.reportId = UUID.randomUUID().toString();
        }
        reportData.type = type;
        reportData.adResponse = adResponse;
        reportData.adError = (adError == null ? AdError.EMPTY : adError);
        reportData.action = action;

        return reportData;
    }

    public ReportData appendExtParameters(JSONObject extParams){
        this.appendParams = extParams;
        return this;
    }

    public ReportData appendParameter(String key,String value){
        try {
            this.appendParams.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AdError getAdError() {
        return adError;
    }

    public void setAdError(AdError adError) {
        this.adError = adError;
    }

    public void setAdResponse(AdResponse adResponse) {
        this.adResponse = adResponse;
    }

    public AdResponse getAdResponse() {
        return adResponse;
    }

    private JSONObject makeSimpleData() throws JSONException {
        JSONObject trackJson = new JSONObject();

        trackJson.put("reportId",reportId);
        trackJson.put("category",this.type);
        trackJson.put("action",this.action);

        if(adResponse != null){
            reportId = adResponse.getClientRequest().getRequestId();
            trackJson.put("adType",adResponse.getClientRequest().getAdType().getStringValue());
            trackJson.put("channel", adResponse.getClientRequest().getCodeId());
            trackJson.put("count", adResponse.getClientRequest().getAdRequestCount());
            trackJson.put("res_count", adResponse.getResponseFeedlistCount());
            try {
                trackJson.put("apiOrSdkAdType",adResponse.getResponseData().getValidConfigBeans().getSource());
            } catch (AdSdkException e) {
//                e.printStackTrace();
            }
        }

        trackJson.put("time",System.currentTimeMillis());
        trackJson.put("version",AdConfig.getDefault().getSdkVersion());
        trackJson.put("errorCode",adError.getErrorCode());
        String errorMessage = adError.getErrorMessage();
        String extErrorMessage = adError.getExtMessage();
        trackJson.put("message",errorMessage+(TextUtils.isEmpty(extErrorMessage) ? "" : "__" + extErrorMessage));

        if(SdkHelper.hasPoint(adResponse.getClientRequest())){
            PointF point = SdkHelper.getPoint(adResponse.getClientRequest());
            trackJson.put("point","x="+point.x+",y="+point.y);
        }

        IReportService reportService = ServiceManager.getService(IReportService.class);
        try {
            trackJson.put("reportErrorCount",reportService.getErrorCountToday(action));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trackJson;
    }

    public String getReportId() {
        return reportId;
    }

    public JSONObject buildReportJson(){
        JSONObject reportJsonData = null;
        try {
            reportJsonData = makeSimpleData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(reportJsonData!=null) {

            if(this.appendParams != null){
                Iterator<String> iter = this.appendParams.keys();
                while(iter.hasNext()){
                    String key = iter.next();
                    try {
                        reportJsonData.put(key,appendParams.getString(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                reportJsonData.put("deviceId", DeviceHelper.getDeviceId(AdClientContext.getClientContext()));
                reportJsonData.put("pkgName", AdClientContext.getClientContext().getPackageName());
                reportJsonData.put("app_version", AppHelper.getVersionName(AdClientContext.getClientContext()));
                reportJsonData.put("imei", DeviceHelper.getImei(AdClientContext.getClientContext()));
                reportJsonData.put("phone_brand", Build.BRAND);
                reportJsonData.put("phone_model", Build.MODEL);
                // 设置操作系统版本
                String osVersion = Build.VERSION.RELEASE;
                if (osVersion.length() == 1) {
                    osVersion = osVersion + ".0.0";
                }
                if (osVersion.length() == 3) {
                    osVersion = osVersion + ".0";
                }
                reportJsonData.put("os_version", osVersion);
                int networkType = NetworkHelper.getNetworkState(AdClientContext.getClientContext());
                reportJsonData.put("network_type", NetworkHelper.getNetworkTypeString(networkType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return reportJsonData;
    }

    public void startReport() {
        Logger.i("ReportData.startReport()","   当前: "+this.action);
        if (this.action != null && this.action.startsWith("dcd_")){
            String[] dcdgroup = AdConfig.getDefault().getServerInitConfig().getDcdgroup();
            if (dcdgroup!=null){
                List<String> strings = Arrays.asList(dcdgroup);
                if (strings.contains(this.action)){
                    IReportServiceHelper.report(this);
                }else {
                    Logger.i("ReportData.startReport()","***服务器开关关闭***");
                }
            }else {
                Logger.i("ReportData.startReport()","***服务器没有给出开关***");
            }
        }else {
            IReportServiceHelper.report(this);
        }
    }

    public void startReport(Listener listener) {
        IReportServiceHelper.report(this,listener);
    }

}
