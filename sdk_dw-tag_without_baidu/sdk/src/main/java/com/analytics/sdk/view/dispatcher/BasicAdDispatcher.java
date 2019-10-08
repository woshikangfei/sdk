package com.analytics.sdk.view.dispatcher;

import android.support.annotation.MainThread;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdCommonListener;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AdErrorFactory;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.ISpamService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.activity.SlideUnlockActivity;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.AdRequestEventListener;

import java.util.Iterator;

/**
 * 主要职责：
 * 1. 负责加载ydt服务器数据并解码生成业务对象ResponseData
 * 2. 统一注册与解除注册事件监听器(要监听哪些事件由具体实现决定)
 * 3. 根据服务器返回获取对应的AdHandler交给具体的实现(由具体实现来决定何时调用)
 * 4. 统一释放资源
 */
public abstract class BasicAdDispatcher extends AdRequestEventListener {

    static final String TAG = BasicAdDispatcher.class.getSimpleName();

    protected EventActionList adRequestEventActionList;
    protected AdHandler adHandler;
    private Iterator<ConfigBeans> adConfigIterator;
    protected AdListeneable adListeneable;

    public BasicAdDispatcher(AdRequest adRequest) {
        super(adRequest);
    }

    /**
     * 当接收到指定处理器的事件时如何处理？ 由具体子类实现
     */
    protected abstract boolean onReceiveEventAction(String action, AdResponse adResponse, Object arg2);
    /**
     * 由具体子类告诉basic需要监听哪些事件
     */
    protected abstract EventActionList buildEventActionList();
    /**
     * 将指定处理器给子类，由子类决定何时调用？
     */
    protected abstract void dispatchErrorResponse(AdRequest adRequest,AdError adError,AdListeneable adListeneable);
    @MainThread
    protected abstract void executeAdHandler(AdHandler adHandler,AdResponse adResponse,AdListeneable adListeneable) throws AdSdkException;

    /**
     * 处理请求
     */
    public boolean dispatchRequest(final AdListeneable adListeneable) {

        //拦截请求可分发！处理校验
        if(interceptRequest(adRequest,adListeneable)) {
            Logger.forcePrint(TAG,"intercepted AdRequest");
            return false;
        }

        this.adListeneable = adListeneable;

        //异步执行任务， 为了不影响主线程UI渲染 ， 例如：开屏，尽快的让view attach.
        //加载默认代码位或者缓存数据
        ThreadExecutor.runOnAndroidHandlerThread(new AdRequestTask(adRequest,adListeneable));
        //获取当前request对应的热力图
        ThreadExecutor.runOnCachedThreadPool(new RequestClientMapTask(adRequest));
        return true;
    }

    /**
     * 接收到指定处理器的事件
     */
    @Override
    public boolean onHandleAction(String action, AdResponse adResponse, Object arg2) {

        if(AdEventActions.ACTION_AD_ERROR.equals(action)) {

            if(handleNext(adResponse,adListeneable)){
                return true;
            }

        }

        return onReceiveEventAction(action,adResponse,arg2);
    }

    protected boolean handleNext(AdResponse adResponse,AdListeneable adListeneable){

        if(isSupportSerialCall() && adConfigIterator.hasNext()){

            ConfigBeans nextAdConfig = adConfigIterator.next();

            Logger.i(TAG,"handle next AdRequest , config = " + nextAdConfig);

            AdRequest clientRequest = adResponse.getClientRequest();

            AdRequest newAdRequest = SdkHelper.buildNextRequest(clientRequest);

            ResponseData newResponseData = ResponseData.forceObtain(newAdRequest,nextAdConfig);
            AdResponse newAdResponse = AdResponse.obtain(newAdRequest,newResponseData);
            newAdRequest.setRecycler(this);

            IAdService adService = ServiceManager.getService(IAdService.class);
            final AdHandler adHandler = adService.getAdHandler(newAdResponse);

            this.adRequest = newAdRequest;
            this.adHandler = adHandler;

            IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
            adStrategyService.applyStrategy(newAdRequest);

            try {
                executeAdHandler(adHandler,newAdResponse,adListeneable);
                return true;
            } catch (AdSdkException e) {
                e.printStackTrace();
            }

            return false;
        }

        return false;
    }

    @Override
    public boolean recycle() {

        super.recycle();

        if(adRequestEventActionList != null){
            EventScheduler.deleteEventListener(adRequestEventActionList,this);
            adRequestEventActionList = null;
        }

        if(this.adHandler != null){
            this.adHandler.recycle();
            this.adHandler = null;
        }

        if(this.adListeneable != null){
            this.adListeneable = null;
        }

        if(this.adConfigIterator != null){
            this.adConfigIterator = null;
        }

        Logger.i(TAG,"recycle EventScheduler listener size = " + EventScheduler.listenerSize());
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
     * 请求点击热力图
     */
    private class RequestClientMapTask implements Runnable {

        private AdRequest adRequest;

        public RequestClientMapTask(AdRequest adRequest) {
            this.adRequest = adRequest;
        }

        @Override
        public void run() {

            IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
            try {
                adStrategyService.requestClickMap(adRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean interceptRequest(AdRequest adRequest, AdListeneable adListeneable) {

        if(adRequest.isRecycled()) {
            dispatchErrorResponseOnMainThread(adRequest,new AdError(ErrorCode.OS.ERROR_AD_REQUEST_RECYCLED,ErrorMessage.OS.ERROR_ADREQUEST_RECYCLED),adListeneable);
            return true;
        }

        if(AdConfig.getDefault().isForceDisableSpam()) {
            return false;
        }

        ISpamService spamService = ServiceManager.getService(ISpamService.class);

        if(spamService.isHitBlack()) { //当前设置认定为作弊
            Logger.i(TAG,"hit spam, dispatchRequest abort");
            notifyClientSparmError(adListeneable);
            return true;
        }

        boolean isHitGray = spamService.isHitGray();

        if(isHitGray){ //如果当前设备不是开屏并且命中灰色
            SlideUnlockActivity.start(AdClientContext.getClientContext());
            return true;
        }

        if(spamService.isGtExposureMaxCount(adRequest.getCodeId())){
            notifyClientSparmError(adListeneable);
            return true;
        }

        return false;
    }

    private void notifyClientSparmError(AdListeneable adListeneable) {
        if(adListeneable instanceof AdCommonListener){
            AdCommonListener adCommonListener = (AdCommonListener) adListeneable;
            AdError adError = AdErrorFactory.factory().create(ErrorCode.SPAM);
            adCommonListener.onAdError(adError);
            ReportData.obtain(adError,AdEventActions.Spam.ACTION_AD_SPAM).startReport();
        }
    }

    public void dispatchSuccessResponse(final AdResponse adResponse,final AdListeneable adListeneable){

        Logger.i(TAG,"dispatchSuccessResponse enter");

        IAdService adService = ServiceManager.getService(IAdService.class);

        final AdHandler adHandler = adService.getAdHandler(adResponse);

        //将当前处理器与AdRequest绑定
        adResponse.getClientRequest().setRecycler(this);

        this.adRequestEventActionList = buildEventActionList();
        this.adHandler = adHandler;

        this.adConfigIterator = adResponse.getResponseData().getParams().iterator();
        if(this.adConfigIterator != null && this.adConfigIterator.hasNext()){
            adConfigIterator.next();
        }

        if(this.adRequestEventActionList != null){
            EventScheduler.addEventListener(adRequestEventActionList,this);
        }

        //SDK | API
        final String reportAction = adResponse.getReportType();

        try {
            AdRequest adRequest = adResponse.getClientRequest();
            if(!adRequest.isRecycled()){ //如果已经被回收了！

                AdRequestQueue.getDefault().push(adResponse);

                if(isExecuteAdHandlerOnMainThread()){
                    //在主线程中执行
                    dispatchSuccessResponseOnMainThread(adHandler,adResponse,adListeneable);
                } else {
                    //在当前线程（cached thread pool中），也就是子线程中执行
                    executeAdHandler(adHandler,adResponse,adListeneable);
                }
            } else {
                dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(ErrorCode.OS.ERROR_AD_REQUEST_RECYCLED,ErrorMessage.OS.ERROR_ADREQUEST_RECYCLED),adListeneable);
            }
        } catch (AdSdkException e) {
            e.printStackTrace();
            ReportData.obtain(new AdError(e.getCode(),e.getMessage()),AdEventActions.ACTION_AD_ERROR,reportAction,adResponse).startReport();
            dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(e.getCode(),e.getMessage()),adListeneable);
        }

    }

    /**
     * AdHandler处理器在子线程中运行，还是在主线程中运行
     */
    protected boolean isExecuteAdHandlerOnMainThread(){
        return true;
    }

    /**
     * 是否支持串行调用
     */
    protected boolean isSupportSerialCall(){
        return false;
    }

    void dispatchErrorResponseOnMainThread(final AdRequest adRequest,final AdError adError,final AdListeneable adListeneable) {
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ReportData.obtain(adError,AdEventActions.ACTION_AD_ERROR,AdResponse.obtain(adRequest)).startReport();
                dispatchErrorResponse(adRequest,adError,adListeneable);
            }
        });
    }

    void dispatchSuccessResponseOnMainThread(final AdHandler adHandler,final AdResponse adResponse,final AdListeneable adListeneable) {
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeAdHandler(adHandler,adResponse,adListeneable);
                } catch (AdSdkException e) {
                    dispatchErrorResponseOnMainThread(adResponse.getClientRequest(),new AdError(e.getCode(),e.getMessage()),adListeneable);
                }
            }
        });
    }


    @Override
    public int getPriority() {
        return 0;
    }
}
