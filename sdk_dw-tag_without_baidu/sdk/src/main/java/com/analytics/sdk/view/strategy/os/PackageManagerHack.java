package com.analytics.sdk.view.strategy.os;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.util.Log;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.view.strategy.crack.ProxyActivity;
import com.analytics.sdk.view.strategy.crack.ProxyContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PackageManagerHack implements InvocationHandler {

    private static final String TAG = PackageManagerHack.class.getSimpleName();

    private Object mPackageManager;
    static boolean sCalled = false;

    public static void enable(){
        sCalled = true;
    }

    public static void disable(){
        sCalled = false;
    }

    public PackageManagerHack(Object packageManager) {
        this.mPackageManager = packageManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Logger.i(TAG,"methodName = " + methodName + " , sCalled = " + sCalled);

        if(sCalled){
            if("resolveIntent".equals(methodName)) {

                replaceResolveIntentPackage(args,"resolveIntent");

            } else if("resolveService".equals(methodName)) {

                replaceResolveIntentPackage(args,"resolveService");

            } else if("getPackageInfo".equals(methodName)) {
                Object packageNameObject = args[0];
                Log.i(TAG,"getPackageInfo packageNameObject = " + packageNameObject);

                if(packageNameObject != null){

                    if(AndroidHackHelper.hasInjectContext(packageNameObject.toString())) {

                        ProxyContext proxyContext = AndroidHackHelper.getInjectProxyContext(packageNameObject.toString());

                        String realPackage = AdClientContext.getClientContext().getPackageName();

                        Log.i(TAG,"getPackageInfo reset = " + realPackage);
                        args[0] = realPackage;

                        PackageInfo packageInfo = (PackageInfo) method.invoke(this.mPackageManager, args);
                        packageInfo.packageName = proxyContext.getPackageName();

                        Log.i(TAG,"getPackageInfo reset2 = " + packageInfo.packageName);

                    }
                }

            } else if("getInstalledApplications".equals(methodName)) {

                try {
                    Object result = method.invoke(this.mPackageManager, args);

                    List<ApplicationInfo> applicationInfoList = null;

                    Log.i(TAG,"getInstalledApplications result = " + result.getClass().getName());

                    if(result instanceof ParceledListSlice){
                        applicationInfoList = ((ParceledListSlice) result).getList();
                    } else {
                        applicationInfoList = (List<ApplicationInfo>) result;
                    }

                    if(applicationInfoList != null){
                        int size = applicationInfoList.size();

                        ApplicationInfo realApplicationInfo = null;

                        for (int i = 0;i < size;i++){

                            ApplicationInfo applicationInfo = applicationInfoList.get(i);

                            if(applicationInfo.packageName.equals(AdClientContext.getClientContext().getPackageName())) {
                                realApplicationInfo = applicationInfo;
                                break;
                            }
                        }

                        if(realApplicationInfo != null){

                            Map<String,ProxyContext> map = AndroidHackHelper.getInjectPackageProxyContextMap();

                            if(map != null && map.size() > 0){

                                for(Iterator<Map.Entry<String,ProxyContext>> iter = map.entrySet().iterator();((Iterator) iter).hasNext();){

                                    Map.Entry<String,ProxyContext> me = iter.next();
                                    ProxyContext proxyContext = me.getValue();
                                    ApplicationInfo disRealApplicationInfo = new ApplicationInfo(realApplicationInfo);
                                    disRealApplicationInfo.packageName = proxyContext.getPackageName();
                                    disRealApplicationInfo.processName = proxyContext.getPackageName();

                                    Log.i(TAG,"getInstalledApplications add disreal");

                                    applicationInfoList.add(disRealApplicationInfo);

                                }
                            }

                        }

                        return result;

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        try {
            // sometimes system binder has problems.
            return method.invoke(this.mPackageManager, args);
        } catch (Throwable th) {
            th.printStackTrace();
            Log.i(TAG,"invoke exception = " + th.getMessage());
            throw th;
        }

    }

    private void replaceResolveIntentPackage(Object[] args, String methodName) {
        Object intentObject = args[0];
        if(intentObject != null && intentObject instanceof Intent) {
            Intent intent = (Intent) intentObject;

            ComponentName componentName = intent.getComponent();

            if(componentName != null){
                String packageName = intent.getComponent().getPackageName();

                if(AndroidHackHelper.hasInjectContext(packageName)) { //当前启动的包名称为被破解所使用的包名则需要替换成真实的包名
                    intent.setComponent(new ComponentName(AdClientContext.getClientContext(),componentName.getClassName()));
                    intent.setPackage(AdClientContext.getClientContext().getPackageName());
                    Log.i(TAG,methodName + " pacakgeName = " + intent.getComponent().getPackageName());
                    args[0] = intent;
                }

            }

        }
    }


}
