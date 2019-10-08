package com.analytics.sdk.dynamic.a;

import android.support.annotation.MainThread;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.dynamic.common.DataSource;
import com.analytics.sdk.dynamic.common.ErrorCode;
import com.analytics.sdk.dynamic.common.ErrorMessage;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.handler.AdHandler;

/**
 * 主要职责：
 * 1. 负责加载ydt服务器数据并解码生成业务对象ResponseData
 * 2. 根据服务器返回获取对应的AdHandler并调用同时上报
 */
public abstract class BasicAdDispatcher {

    static final String TAG = BasicAdDispatcher.class.getSimpleName();

    public boolean dispatchRequest(final AdRequest adRequest, final AdListeneable adListeneable) {

        //拦截请求可分发！处理校验
        if(interceptRequest(adRequest,adListeneable)) {
            Logger.forcePrint(TAG,"intercepted AdRequest");
            return false;
        }

        //异步执行任务， 为了不影响主线程UI渲染 ， 例如：开屏，尽快的让view attach.
        //加载默认代码位或者缓存数据
        ThreadExecutor.runOnAndroidHandlerThread(new AdRequestTask(adRequest,adListeneable));
        //请求服务器并更新缓存(目前这个任务只是为了更新缓存)
//        ThreadExecutor.runOnCachedThreadPool(new UpdateCacheFromRequestServerTask(adRequest,adListeneable));
        return true;
    }

    /**
     * 加载默认代码位或者缓存数据
     */
    // TODO: 2019/7/10 先不提取BasicTask，这两个任务之间共性的量不足提取，后续根据需求进行重构
    private class AdRequestTask implements Runnable {

        private AdRequest adRequest;
        private AdListeneable adListeneable;

        public AdRequestTask(AdRequest adRequest,AdListeneable adListeneable) {
            this.adRequest = adRequest;
            this.adListeneable = adListeneable;
        }

        @Override
        public void run() {

            IAdService adService = ServiceManager.getService(IAdService.class);
            adService.loadAdData(adRequest, new Listener<AdResponse,String>(){

                /**
                 * 成功从缓存中或者获取到默认配置
                 */
                @Override
                public boolean onSuccess(SuccessMessage<AdResponse> successMessage) {
                    boolean handleResult =  super.onSuccess(successMessage);
                    Logger.i(TAG,"AdRequestTask onSuccess enter , data = " + successMessage.getData());

                    final AdResponse adResponse = successMessage.getData();

                    //TODO:拦截日志上报
                    if(interceptResponse(adResponse,adListeneable)){
                        Logger.forcePrint(TAG,"AdRequestTask intercepted AdResponse");
                        return false;
                    }

                    dispatchSuccessResponse(adResponse,adListeneable);

                    return handleResult;
                }

                @Override
                public boolean onError(ErrorMessage<String> errorMessage) {
                    Logger.i(TAG,"AdRequestTask onError enter , data = " + errorMessage.getData());
                    boolean handleResult = super.onError(errorMessage);
                    dispatchErrorResponseOnMainThread(adRequest,new AdError(errorMessage.getCode(),errorMessage.getMessage()),adListeneable);
                    return handleResult;
                }
            });

        }
    }

    /**
     * 1.请求服务器并更新缓存
     */
    @Deprecated
    private class UpdateCacheFromRequestServerTask implements Runnable {

        private AdRequest adRequest;
        private AdListeneable adListeneable;

        public UpdateCacheFromRequestServerTask(AdRequest adRequest, AdListeneable adListeneable) {
            this.adRequest = adRequest;
            this.adListeneable = adListeneable;
        }

        @Override
        public void run() {

            IAdService adService = ServiceManager.getService(IAdService.class);
            adService.requestServerAdData(adRequest, new Listener<AdResponse,String>(){

                /**
                 * 成功向ydt服务器获取到数据(数据可能是： 1. 三方SDK的配置。  2. API返回的广告)
                 */
                @Override
                public boolean onSuccess(SuccessMessage<AdResponse> successMessage) {
                    boolean handleResult =  super.onSuccess(successMessage);
                    Logger.i(TAG,"UpdateCacheFromRequestServerTask onSuccess enter , data = " + successMessage.getData());

                    final AdResponse adResponse = successMessage.getData();

                    if(interceptResponse(adResponse,adListeneable)){
                        Logger.forcePrint(TAG,"UpdateCacheFromRequestServerTask intercepted AdResponse");
                        return false;
                    }

                    //保留一份这个数据, 如果API要使用呢？
                    // FIXME: 2019/7/10 如果没有使用API的数据，这份数据就是内心泄漏(目前迅雷会使用这种方式)

                    return handleResult;
                }

                @Override
                public boolean onError(ErrorMessage<String> errorMessage) {
                    Logger.i(TAG,"UpdateCacheFromRequestServerTask onError enter , data = " + errorMessage.getData());
                    return false;
                }
            });

        }
    }

    private boolean interceptRequest(AdRequest adRequest, AdListeneable adListeneable) {

        if(adRequest.isRecycled()) {
            dispatchErrorResponseOnMainThread(adRequest,new AdError(ErrorCode.OS.ERROR_AD_REQUEST_RECYCLED,ErrorMessage.OS.ERROR_ADREQUEST_RECYCLED),adListeneable);
            return true;
        }

//        //检查当前网络
//        if(!NetworkHelper.isNetworkAvailable(activity)) {
//            dispatchErrorResponseOnMainThread(adRequest,new AdError(ErrorCode.OS.ERROR_NETWORK_DISCONNECTED,ErrorMessage.OS.ERROR_NETWORK_DISCONNECTED),adListeneable);
//            return true;
//        }

//        //验证权限
//        if(!PermissionsHelper.isGrantExternalRW(activity)) {
//            dispatchErrorResponseOnMainThread(adRequest,new AdError(ErrorCode.OS.ERROR_PERMISSION,ErrorMessage.OS.ERROR_PERMISSION),adListeneable);
//            return true;
//        }

        return false;
    }

    /**
     * 主要负责验证返回的数据是否正确
     */
    private boolean interceptResponse(AdResponse adResponse, AdListeneable adListeneable) {

        ResponseData responseData = adResponse.getResponseData();

        //返回的对象为无响应对象
        if(ResponseData.NO_RESPONSE == responseData){
            dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.ApiServer.ERROR_SERVER_PARAMS,ErrorMessage.Ad.ERROR_GET_ADS),adListeneable);
            return true;
        }

        //如果不是sdk处理也不是API处理,则失败。
//        if(!responseData.isSdkSource() && !responseData.isApiSource()){
//            dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.ApiServer.ERROR_AD_SOURCE_NOT_FOUND,ErrorMessage.Ad.ERROR_GET_ADS),adListeneable);
//            return true;
//        }

        //如果是sdk处理，但找不到对应的sdk配置
        if(responseData.isSdkSource()) {

            if(!responseData.has3rdSdkConfig()) {
                dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.ApiServer.ERROR_AD_3rdSDK_CONFIG_NULL,ErrorMessage.Ad.ERROR_GET_ADS),adListeneable);
                return true;
            }

            try {
                ConfigBeans configBeans = responseData.getValidConfigBeans();

                int source = configBeans.getSource();

                if(DataSource.SDK_CSJ != source && DataSource.SDK_GDT != source && DataSource.SDK_BAIDU != source) {
                    dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.ApiServer.ERROR_AD_SOURCE_NOT_FOUND,ErrorMessage.Ad.ERROR_GET_ADS),adListeneable);
                    return true;
                }

            } catch (AdSdkException e) {
                e.printStackTrace();
                dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.ApiServer.ERROR_AD_SOURCE_NOT_FOUND,ErrorMessage.Ad.ERROR_GET_ADS),adListeneable);
                return true;
            }

        }

        return false;
    }

    public void dispatchSuccessResponse(final AdResponse adResponse,final AdListeneable adListeneable){

        Logger.i(TAG,"dispatchSuccessResponse enter");

        IAdService adService = ServiceManager.getService(IAdService.class);

        final AdHandler adHandler = adService.getAdHandler(adResponse);

        //将对应的处理器与AdHandler绑定
        adResponse.getClientRequest().setRecycler(adHandler);

        //SDK | API
        final String reportAction = adResponse.getReportType();
        ReportData.obtain(IReportService.Action.ACTION_AD_REQUEST,adResponse).startReport();
        try {
            AdRequest adRequest = adResponse.getClientRequest();
            if(!adRequest.isRecycled()){ //如果已经被回收了！
                if(isExecuteAdHandlerOnMainThread()){
                    //在主线程中执行
                    dispatchErrorResponseOnMainThread(adHandler,adResponse,adListeneable);
                } else {
                    //在当前线程（cached thread pool中），也就是子线程中执行
                    executeAdHandler(adHandler,adResponse,adListeneable);
                }
            } else {
                dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.OS.ERROR_AD_REQUEST_RECYCLED,ErrorMessage.OS.ERROR_ADREQUEST_RECYCLED),adListeneable);
            }
        } catch (AdSdkException e) {
            e.printStackTrace();
            ReportData.obtain(new AdError(e.getCode(),e.getMessage()),IReportService.Action.ACTION_AD_ERROR,reportAction,adResponse).startReport();
            dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(e.getCode(),e.getMessage()),adListeneable);
        }

    }


    protected boolean isExecuteAdHandlerOnMainThread(){
        return true;
    }

    void dispatchErrorResponseOnMainThread(final AdHandler adHandler,final AdResponse adResponse,final AdListeneable adListeneable) {
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeAdHandler(adHandler,adResponse,adListeneable);
                } catch (AdSdkException e) {
                    ReportData.obtain(new AdError(e.getCode(),e.getMessage()),IReportService.Action.ACTION_AD_ERROR,adResponse.getReportType(),adResponse).startReport();
                    dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(e.getCode(),e.getMessage()),adListeneable);
                }
            }
        });
    }

    @MainThread
    public abstract void executeAdHandler(AdHandler adHandler,AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException;

    void dispatchErrorResponseOnMainThread(final AdRequest adRequest,final AdError adError,final AdListeneable adListeneable) {
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dispatchErrorResponse(adRequest,adError,adListeneable);
            }
        });
    }

    public abstract void dispatchErrorResponse(AdRequest adRequest,AdError adError,AdListeneable adListeneable);

}
