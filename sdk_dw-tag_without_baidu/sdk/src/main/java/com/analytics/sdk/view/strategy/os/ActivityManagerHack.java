package com.analytics.sdk.view.strategy.os;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.activity.ActivityTaskManager;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.view.strategy.crack.ProxyActivity;
import com.analytics.sdk.view.strategy.crack.ProxyContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ActivityManagerHack implements InvocationHandler {

    private static final String TAG = ActivityManagerHack.class.getSimpleName();

    private IActivityManager mActivityManager;

    public ActivityManagerHack(IActivityManager activityManager) {
        this.mActivityManager = activityManager;
    }

    public static Activity splashActivity = null;

    public static void tryFinishSplashActivity(){
        if(splashActivity != null){
            splashActivity.finish();
            splashActivity = null;
        }
    }

    public static boolean isSplashFinishActivity(){
        if(splashActivity!=null){
            return true;
        }
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        Log.i(TAG,"methodName = " + methodName);

        if("finishActivity".equals(methodName)) {

            final Activity activity = ActivityTaskManager.getInstance().peekTopActivity();

            if(AndroidHack.isBlockStartActivity && HackPackageMapping.isSplashActivity(activity)){

                Logger.i(TAG,"*** hack finishActivity , top activity = " + activity.getClass().getName());

                splashActivity = activity;
                //不允许退出，等首页进去之后在退出

                if(AdConfig.getDefault().isPrintLog()) {
//                    new Exception().printStackTrace();
                }

                return false;
            }

        } else if("startActivity".equals(methodName)) {

            Logger.i(TAG,"*** hack startActivity ");
            if(AdConfig.getDefault().isPrintLog()) {
//                new Exception().printStackTrace();
            }

        } else if("getContentProvider".equals(methodName)) {
            Logger.i(TAG,"*** getContentProvider arg = " + args.length);
            if(args != null && args.length > 1){
                Object packageNameObject = args[1];
                Logger.i(TAG,"*** getContentProvider arg = " + packageNameObject);

                if(packageNameObject != null){

//                    if(AndroidHackHelper.hasInjectContext(packageNameObject.toString())) {
//
//                        ProxyContext proxyContext = AndroidHackHelper.getInjectProxyContext(packageNameObject.toString());
//
//                        String realPackage = AdClientContext.getClientContext().getPackageName();
//
//                        Log.i(TAG,"getPackageInfo reset = " + realPackage);
//
//                    }
                }

            }

        } else if("getContentProviderExternal".equals(methodName)) {
//            Logger.i(TAG,"*** getContentProviderExternal arg = " + args[0]);
        }

        try {
            // sometimes system binder has problems.
            return method.invoke(this.mActivityManager, args);
        } catch (Throwable th) {
            Throwable c = th.getCause();
            if (c != null && c instanceof DeadObjectException) {
                // retry connect to system binder
                IBinder ams = ServiceManager.getService(Context.ACTIVITY_SERVICE);
                if (ams != null) {
                    IActivityManager am = ActivityManagerNative.asInterface(ams);
                    mActivityManager = am;
                }
            }

            Throwable cause = th;
            do {
                if (cause instanceof RemoteException) {
                    throw cause;
                }
            } while ((cause = cause.getCause()) != null);

            throw c != null ? c : th;
        }

    }


}
