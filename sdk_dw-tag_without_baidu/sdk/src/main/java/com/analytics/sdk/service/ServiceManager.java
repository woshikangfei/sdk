package com.analytics.sdk.service;

import android.content.Context;
import android.text.TextUtils;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.IAdServiceImpl;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.IAdStrategyServiceImpl;
import com.analytics.sdk.service.ad.ISpamServiceImpl;
import com.analytics.sdk.service.ad.ISpamService;
import com.analytics.sdk.service.client.IClientServcie;
import com.analytics.sdk.service.client.IClientServcieImpl;
import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.download.IDownloadServiceImpl;
import com.analytics.sdk.service.download.IDownloadService;
import com.analytics.sdk.service.report.IReportService;
import com.analytics.sdk.service.report.IReportServiceImpl;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理所有服务
 */
public final class ServiceManager {

    static final String TAG = ServiceManager.class.getSimpleName();

    private ServiceManager(){
    }

    /**
     * 存储所有服务
     */
    static final ConcurrentHashMap<String,IService> sServiceContainer = new ConcurrentHashMap<>();

    public static void init(Context context){
        //从服务器获取数据
        IService adService = new IAdServiceImpl();
        adService.init(context);

        //埋点数据上报
        IService reportService = new IReportServiceImpl();
        reportService.init(context);

        //像开发者提供所有业务接口
        IService clientService = new IClientServcieImpl();
        clientService.init(context);

        //误点
        IService adStrategyService = new IAdStrategyServiceImpl();
        adStrategyService.init(context);

        //反作弊
        IService spamService = new ISpamServiceImpl();
        spamService.init(context);

        //下载
        IService iErrorServiceService = new IDownloadServiceImpl();
        iErrorServiceService.init(context);


        putService(IAdService.class,adService);
        putService(IReportService.class,reportService);
        putService(IClientServcie.class,clientService);
        putService(IAdStrategyService.class,adStrategyService);
        putService(ISpamService.class,spamService);
        putService(IDownloadService.class,iErrorServiceService);

    }

    static boolean isRealy(){
        return AdClientContext.isRealy();
    }

    public static <R> R getService(Class<? extends IService> serviceClass) {

        if(!isRealy()){
            throw new AdSdkRuntimeException("must call SDK init(...) method");
        }

        String serviceName = getServiceName(serviceClass);

        if(TextUtils.isEmpty(serviceName)){
            throw new AdSdkRuntimeException("serviceName is null");
        }

        IService service = sServiceContainer.get(serviceName);
        if(service == null){
            throw new AdSdkRuntimeException("service("+serviceName+") not found");
        }
        try {
            return (R) service;
        } catch (Exception e){
            throw new AdSdkRuntimeException(e);
        }
    }

    public static void putService(Class<? extends IService> serviceClass, IService serviceImpl){

        if(!isRealy()){
            throw new AdSdkRuntimeException("must call init(...) method");
        }

        AopInvocationHandler aopInvocationHandler = new AopInvocationHandler(serviceClass,serviceImpl);
        IService service = (IService) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass}, aopInvocationHandler);
        sServiceContainer.put(getServiceName(serviceClass),service);
    }

    public static void putService(Class<? extends IService> serviceClass){
        putService(serviceClass,null);
    }

    public static void dump(){
        for(Iterator<Map.Entry<String,IService>> iter = sServiceContainer.entrySet ().iterator();iter.hasNext();){
            Map.Entry<String,IService> entry = iter.next();
            String key = entry.getKey();
            IService value = entry.getValue();
            Logger.i(TAG,"key = " + key + " , value = " + value);
        }
    }

    public static String getServiceName(Class<?> serviceClass) {
        return serviceClass.getName();
    }

}
