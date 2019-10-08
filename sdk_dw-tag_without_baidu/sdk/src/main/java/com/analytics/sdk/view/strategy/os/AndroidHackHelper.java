package com.analytics.sdk.view.strategy.os;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.Application;
import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Singleton;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.reflect.ReflectHelper;
import com.analytics.sdk.view.strategy.crack.ProxyActivity;
import com.analytics.sdk.view.strategy.crack.ProxyContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class AndroidHackHelper {

    static final String TAG = "hack";
    public static Object sActivityThread;
    public static Object sLoadedApk;
    public static Handler sBaseActivityThreadHandler;
    public static Instrumentation sBaseInstrumentation;
    public static Instrumentation sCurrentInstrumentation;
    public static InstrumentationHack sHackInstrumentation;
    public static Application sApplication;
    /**
     * 这可能存在内存泄漏
     */
    static Map<String,ProxyContext> sPackageProxyContext = new HashMap<>();

    public static Map<String,ProxyContext> getInjectPackageProxyContextMap(){
        return sPackageProxyContext;
    }

    public static ProxyContext removeInjectProxyContext(String packageName){
        Logger.i(TAG,"removeInjectProxyContext enter , packageName = " + packageName);
        if(sPackageProxyContext.containsKey(packageName)){
            ProxyContext proxyContext = sPackageProxyContext.get(packageName);
            proxyContext.disable();
            return proxyContext;
        }
        return null;
    }

    public static void injectInstrumentation(Context context) throws Exception{
        if(sHackInstrumentation == null){
            try {
                Object activityThreadObj = AndroidHackHelper.getActivityThread(context);
                Instrumentation baseInstrumentation = AndroidHackHelper.getInstrumentation(activityThreadObj);
                sBaseInstrumentation = baseInstrumentation;
                sHackInstrumentation = new InstrumentationHack(baseInstrumentation);
                AndroidHackHelper.injectInstrumentation(activityThreadObj, sHackInstrumentation);
                Logger.i(TAG,"injectInstrumentation success");
            } catch (Exception t){
                t.printStackTrace();
                Logger.i(TAG,"injectInstrumentation exception = " + t.getMessage());
                throw t;
            }
        }
    }

    public static void injectActivityThreadHandler(Context context) throws Exception {
        if(sBaseActivityThreadHandler == null){
            try {
                Object activityThreadObj = AndroidHackHelper.getActivityThread(context);

                Handler mainHandler = (Handler) ReflectHelper.invoke(activityThreadObj.getClass(),activityThreadObj,"getHandler");

                sBaseActivityThreadHandler = mainHandler;

                ReflectHelper.setField(mainHandler.getClass(), mainHandler, "mCallback", new ActivityThreadHandlerHack());

                Logger.i(TAG,"injectActivityThreadHandler success , mainHandler = " + mainHandler);
            } catch (Exception t){
                t.printStackTrace();
                Logger.i(TAG,"injectActivityThreadHandler exception = " + t.getMessage());
                throw t;
            }
        }
    }

    public static boolean hasInjectContext(String packageName){
        return sPackageProxyContext.containsKey(packageName);
    }

    public static ProxyContext getInjectProxyContext(String packageName){
        return sPackageProxyContext.get(packageName);
    }

    public static boolean injectBaseContext(Activity target,String packageName){
        return injectBaseContext(target, packageName, "");
    }

    public static boolean manageProxyContext(String packageName,ProxyContext proxyContext){
        sPackageProxyContext.put(packageName, proxyContext);
        return true;
    }

    public static boolean injectBaseContext(Activity target,String packageName,String startActivityClassPrefix){

        try {

            ProxyContext proxyContext = null;

            if(sPackageProxyContext.containsKey(packageName)){
                proxyContext = sPackageProxyContext.get(packageName);
                proxyContext.enable();
            } else {
                proxyContext = new ProxyContext(target.getBaseContext(),packageName,startActivityClassPrefix);
            }

            Field field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            field.set(target,proxyContext);

            manageProxyContext(packageName,proxyContext);

            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static Application makeApplication(Instrumentation instrumentation, Context resourceContext) throws Exception {
        if (null != sApplication) {
            return sApplication;
        }

        try {
            sApplication = instrumentation.newApplication(ReflectHelper.class.getClassLoader(), "android.app.Application", resourceContext);
            instrumentation.callApplicationOnCreate(sApplication);
            return sApplication;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"makeApplication exception = " + e.getMessage());
            throw e;
        }
    }

    public static Object getActivityThread(Context base) throws Exception {
        if (sActivityThread == null) {
            sActivityThread = getsActivityThread2(base);
        }

        return sActivityThread;
    }

    public static Object getsActivityThread2(Context base) throws Exception {
        try {
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");

            Object activityThread = null;
            try {
                activityThread = ReflectHelper.getField(activityThreadClazz, null, "sCurrentActivityThread");
            } catch (Exception e) {
                // ignored
            }
            if (activityThread == null) {
                activityThread = ((ThreadLocal<?>) ReflectHelper.getField(activityThreadClazz, null, "sThreadLocal")).get();
            }
            Logger.i(TAG,"getActivityThread success");
            return activityThread;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"getActivityThread exception = " + e.getMessage());
            throw e;
        }
    }


    public static Singleton<IActivityManager> getIActivityManager() throws Exception {
        Singleton<IActivityManager> defaultSingleton;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultSingleton = (Singleton<IActivityManager>) ReflectHelper.getField(ActivityManager.class,null,"IActivityManagerSingleton");
        } else {
            defaultSingleton = (Singleton<IActivityManager>) ReflectHelper.getField(ActivityManagerNative.class,null,"gDefault");
        }
        return defaultSingleton;
    }

    public static void injectIPackageManager(Context context) throws Exception {

        try {
            Object activityThreadObj = AndroidHackHelper.getActivityThread(context);
            Class<?> activityThreadClass = activityThreadObj.getClass();

            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(activityThreadObj);

            Class<?> iPackageMangaerClass = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageMangaerClass.getClassLoader(), new Class<?>[]{iPackageMangaerClass},
                    new PackageManagerHack(sPackageManager));

            sPackageManagerField.set(activityThreadObj, proxy);
            PackageManager pm = context.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);

            Logger.i(TAG,"injectIPackageManager success , packageManagerProxy = " + proxy);

        } catch (Throwable e) {
            e.printStackTrace();
            Logger.i(TAG,"injectIPackageManager error , message = " + e.getMessage());
        }

    }

    public static void injectIActivityManager(Context context) throws Exception {

        try {
            Singleton<IActivityManager> defaultSingleton = getIActivityManager();

            IActivityManager origin = defaultSingleton.get();

            IActivityManager activityManagerProxy = (IActivityManager) Proxy.newProxyInstance(context.getClassLoader(), new Class[] { IActivityManager.class },
                    new ActivityManagerHack(origin));

            ReflectHelper.setField(defaultSingleton.getClass(),defaultSingleton,"mInstance",activityManagerProxy);

            if (defaultSingleton.get() == activityManagerProxy) {
                Logger.i(TAG,"injectIActivityManager success , activityManagerProxy = " + activityManagerProxy);
            } else {
                throw new Exception("injectIActivityManager error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"injectIActivityManager exception = " + e.getMessage());
            throw e;
        }

    }

    public static Instrumentation getInstrumentation(Context base) throws Exception {
        if (getActivityThread(base) != null) {
            try {
                sCurrentInstrumentation = (Instrumentation) ReflectHelper.invoke(
                        sActivityThread.getClass(), sActivityThread, "getInstrumentation");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sCurrentInstrumentation;
    }

    public static Instrumentation getInstrumentation(Object activityThread) throws Exception {
        if (activityThread != null) {
            sCurrentInstrumentation = (Instrumentation) ReflectHelper.invoke(
                    activityThread.getClass(), activityThread, "getInstrumentation");
        }

        return sCurrentInstrumentation;
    }

    public static Instrumentation getInstrumentation2(Object activityThread) throws Exception {
        if (activityThread != null) {
            Instrumentation currentInstrumentation = (Instrumentation) ReflectHelper.invoke(
                    activityThread.getClass(), activityThread, "getInstrumentation");
            return currentInstrumentation;
        }

        return null;
    }

    static void injectInstrumentation(Object activityThread, Instrumentation instrumentation) throws Exception {
        ReflectHelper.setField(activityThread.getClass(), activityThread, "mInstrumentation", instrumentation);
    }

    public static Object getLoadedApk(Context base) throws Exception {
        if (sLoadedApk == null) {
            sLoadedApk = ReflectHelper.getField(base.getClass(), base, "mPackageInfo");
        }
        return sLoadedApk;
    }

}
