package com.analytics.sdk.service;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AopInvocationHandler implements InvocationHandler {

    static final String TAG = AopInvocationHandler.class.getSimpleName();

    private Class<? extends IService> targetServiceClass;
    private Object targetObject;

    public AopInvocationHandler(Class<? extends IService> targetServiceClass, Object targetObject){
        this.targetServiceClass = targetServiceClass;
        this.targetObject = targetObject;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result = null;
        long start = System.currentTimeMillis();

        String methodSign = getMethodSign(method);

        if(tryExecuteBeforeTask(methodSign,method,proxy,args)){
            Logger.i(TAG,"abort execute method("+methodSign+")");
            return null;
        }

        try {
            result = method.invoke(targetObject,args);
        } catch (Throwable e){
            e.printStackTrace();
        }

        tryExecuteAfterTask(methodSign,method,proxy,args);

        long end = System.currentTimeMillis();

        if(AdConfig.getDefault().isPrintLog() && (end - start >= 100)) {
            Logger.i(TAG,"--------------invoke class("+targetServiceClass.getSimpleName()+") method("+method.getName()+") , used time = " + (System.currentTimeMillis() - start) + " ms");
        }
        return result;
    }

    private String getMethodSign(Method method) {
        String serviceClassName = targetServiceClass.getSimpleName();
        String methodName = method.getName();
        String methodSign = serviceClassName+"#"+methodName;
        return methodSign;
    }

    private boolean tryExecuteBeforeTask(String methodSign,Method method, Object proxy,Object[] args) {

        Logger.i(TAG,"tryExecuteBeforeTask enter , method sign = " + methodSign);

        return false;
    }

    private boolean tryExecuteAfterTask(String methodSign,Method method, Object proxy,Object[] args) {

        Logger.i(TAG,"tryExecuteAfterTask enter , method sign = " + methodSign);


        return false;
    }

}
