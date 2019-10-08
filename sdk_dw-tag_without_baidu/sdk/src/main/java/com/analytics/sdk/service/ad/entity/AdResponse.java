package com.analytics.sdk.service.ad.entity;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.report.IReportService;

public class AdResponse {

    /**
     * 这次响应对应的请求
     */
    private AdRequest clientRequest;
    /**
     * 服务器响应的数据
     */
    private ResponseData responseData = ResponseData.NO_RESPONSE;
    private Listener clientListener = Listener.EMPTY;
    private int responseFeedlistCount = 0;

    private AdResponse(){}

    public static AdResponse obtain(AdRequest clientRequest) {
        return obtain(clientRequest,ResponseData.NO_RESPONSE,Listener.EMPTY);
    }

    public static AdResponse obtain(AdRequest clientRequest, ResponseData responseData) {
        return obtain(clientRequest,responseData,Listener.EMPTY);
    }

    public static AdResponse obtain(AdRequest clientRequest, ResponseData responseData, Listener clientListener) {
        AdResponse adResponse = new AdResponse();
        adResponse.clientRequest = clientRequest;
        adResponse.responseData = responseData;
        adResponse.clientListener = clientListener;
        return adResponse;
    }

    public AdRequest getClientRequest() {
        return clientRequest;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public void clear3rdSdkConfig(){
        if(responseData != null){
            responseData.getParams().clear();
        }
    }

    public void resetAdRequest(AdRequest adRequest){
        this.clientRequest = adRequest;
    }

    public Listener getClientListener() {
        return clientListener;
    }

    public String getReportType(){
        if(ResponseData.NO_RESPONSE == getResponseData()) {
            return AdEventActions.ACTION_UNKNOW;
        }
        final String reportType = getResponseData().isSdkSource() ? IReportService.Type.TYPE_SDK : IReportService.Type.TYPE_API;
        return reportType;
    }

    public int getResponseFeedlistCount() {
        return responseFeedlistCount;
    }

    public AdResponse setResponseFeedlistCount(int responseFeedlistCount) {
        this.responseFeedlistCount = responseFeedlistCount;
        return this;
    }
}
