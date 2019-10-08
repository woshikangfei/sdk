package com.analytics.sdk.view.strategy.os;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.IAppTask;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.runtime.activity.ActivityTaskManager;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.dynamic.IDynamicService;
import com.analytics.sdk.view.strategy.crack.ProxyActivity;
import com.analytics.sdk.view.strategy.crack.ProxyContext;

public class InstrumentationHack extends Instrumentation {

    static final String TAG = InstrumentationHack.class.getSimpleName();

    static long DELAY_TIME = 3 * 1000;
    public static int serverDelayTime = 0;
    private Instrumentation mBase;
    public static UIHelper.ActivityState splashActivityState = new UIHelper.ActivityState();

    public InstrumentationHack(Instrumentation base) {
        this.mBase = base;
    }

    @Override
    public void execStartActivityFromAppTask(Context who, IBinder contextThread, IAppTask appTask, Intent intent, Bundle options) {
        injectStartActivityIntent(intent,"execStartActivityFromAppTask");
        mBase.execStartActivityFromAppTask(who, contextThread, appTask, intent, options);
    }

    @Override
    public ActivityResult execStartActivityAsCaller(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options, int userId) {
        injectStartActivityIntent(intent,"execStartActivityAsCaller");
        return mBase.execStartActivityAsCaller(who, contextThread, token, target, intent, requestCode, options, userId);
    }

    @Override
    public ActivityResult execStartActivity(final Context who, final IBinder contextThread, final IBinder token, final Activity target, final Intent intent, final int requestCode) {
        injectStartActivityIntent(intent,"execStartActivity#1");

        if(AndroidHack.isBlockStartActivity && HackPackageMapping.isMainActivity(intent)){
            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBase.execStartActivity(who, contextThread, token, target, intent, requestCode);
                    onDelayExecStartActivity();
                }
            },DELAY_TIME);
            return null;
        } else {
            return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode);
        }
    }

    @Override
    public ActivityResult execStartActivity(final Context who, final IBinder contextThread, final IBinder token, final Activity target, final Intent intent, final int requestCode, final Bundle options) {
        injectStartActivityIntent(intent,"execStartActivity#2");
        if(AndroidHack.isBlockStartActivity && HackPackageMapping.isMainActivity(intent)){
            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
                    onDelayExecStartActivity();
                }
            },DELAY_TIME);
            return null;
        } else {
            return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
        }
    }

    @Override
    public ActivityResult execStartActivity(final Context who, final IBinder contextThread, final IBinder token, final Fragment target, final Intent intent, final int requestCode, final Bundle options) {
        injectStartActivityIntent(intent,"execStartActivity#3");

        if(AndroidHack.isBlockStartActivity && HackPackageMapping.isMainActivity(intent)){
            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
                    onDelayExecStartActivity();
                }
            },DELAY_TIME);
            return null;
        } else {
            return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
        }
    }

    @Override
    public ActivityResult execStartActivity(final Context who, final IBinder contextThread, final IBinder token, final String target, final Intent intent, final int requestCode, final Bundle options) {
        injectStartActivityIntent(intent,"execStartActivity#4");
        if(AndroidHack.isBlockStartActivity && HackPackageMapping.isMainActivity(intent)){
            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
                    onDelayExecStartActivity();
                }
            },DELAY_TIME);
            return null;
        } else {
            return mBase.execStartActivity(who, contextThread, token, target, intent, requestCode, options);
        }
    }

    private void onDelayExecStartActivity() {
        Log.i(TAG,"*** hack delay execStartActivity intent");
        AndroidHack.isBlockStartActivity = false;
        EventScheduler.dispatch(Event.obtain("DelayExecStartActivity.Completed"));
    }

    private void injectStartActivityIntent(Intent intent, String tag){
        Log.i(TAG,"hack injectStartActivityIntent enter("+tag+")");

        isl.getActivityMonitor().execStartActivity(intent);

        DELAY_TIME = serverDelayTime != 0 ? serverDelayTime : SdkHelper.getRandom(1000,1500);

        boolean isMainActivity = HackPackageMapping.isMainActivity(intent);
        boolean isSplashActivity = HackPackageMapping.isSplashActivity(intent);

        if(isSplashActivity){
            splashActivityState.reset();
        }

        ComponentName componentName = intent.getComponent();
        String className = "";
        if(componentName != null){
            className = intent.getComponent().getClassName();

            String packageName = componentName.getPackageName();

            if(AndroidHackHelper.hasInjectContext(packageName)){

                ProxyContext proxyContext = AndroidHackHelper.getInjectProxyContext(packageName);

                Logger.i(TAG,"inject proxyContext = " + proxyContext);

                if(TextUtils.isEmpty(proxyContext.getStartActivityClassPrefix()) ||  !(className != null && className.startsWith(proxyContext.getStartActivityClassPrefix()))) {
                    Logger.i(TAG,"injectStartActivityIntent enter , " + componentName);
                    intent.setClassName(AdClientContext.getClientContext().getPackageName(),className);
                }

            }

        }

        if(AdConfig.getDefault().isPrintLog()){
            Log.i(TAG,"intent packagename = "+intent.getPackage()+" , className = " + className + " , delayTime = " + DELAY_TIME + " , isBlockStartActivity = " + AndroidHack.isBlockStartActivity + " , isMainActivityWaitAdWebActivity = " + AndroidHack.isMainActivityWaitAdWebActivity + ",serverDelayTime = " + serverDelayTime + " , isMainActivity = " + isMainActivity + " , isSplashActivity = " + isSplashActivity);
        }
    }

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Logger.i(TAG,"hack newActivity className = " + className + " , intent = " + intent);
        Activity newActivity = mBase.newActivity(cl, className, intent);
        return newActivity;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void callActivityOnCreate(final Activity activity, Bundle icicle, PersistableBundle persistentState) {

        Logger.i(TAG,"hack activity onCreate className = " + activity.getClass().getName());

        isl.getActivityMonitor().onCallActivityOnCreateMethodBefore(activity);

        ActivityTaskManager.getInstance().pushToActivityStack(activity);

        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.onCreateTime = System.currentTimeMillis();
        }

        mBase.callActivityOnCreate(activity, icicle,persistentState);

        if(HackPackageMapping.isMainActivity(activity)) {
            ActivityManagerHack.tryFinishSplashActivity();
        }

        if(AndroidHack.isMainActivityWaitAdWebActivity && HackPackageMapping.isGQTWebActivity(activity)) {
            ThreadExecutor.runOnAndroidHandlerThread(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                    AndroidHack.isMainActivityWaitAdWebActivity = false;
                }
            },500);
        }
        isl.getActivityMonitor().onCallActivityOnCreateMethodAfter(activity);
    }

    @Override
    public void callActivityOnCreate(final Activity activity, Bundle icicle) {
        Logger.i(TAG,"hack activity onCreate className = " + activity.getClass().getName());
        isl.getActivityMonitor().onCallActivityOnCreateMethodBefore(activity);
        ActivityTaskManager.getInstance().pushToActivityStack(activity);

        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.onCreateTime = System.currentTimeMillis();
        }

        mBase.callActivityOnCreate(activity, icicle);

        if(HackPackageMapping.isMainActivity(activity)) {
            ActivityManagerHack.tryFinishSplashActivity();
            breakCheckThread = true;
            checkMyThread = null;
        }

        if(AndroidHack.isMainActivityWaitAdWebActivity && HackPackageMapping.isGQTWebActivity(activity)) {
            ThreadExecutor.runOnAndroidHandlerThread(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                    AndroidHack.isMainActivityWaitAdWebActivity = false;
                }
            },1500);
        }
        isl.getActivityMonitor().onCallActivityOnCreateMethodAfter(activity);
    }

    private boolean breakCheckThread = false;
    private Thread checkMyThread = null;
    private int checkCount = 0;

    private void checkMyInstrumentation(final Activity activity){

        Logger.i(TAG,"checkMyInstrumentation enter");

        if(!HackPackageMapping.isSplashActivity(activity)) {
            Logger.i(TAG,"checkMyInstrumentation return , not splash");
            return;
        }

        final Instrumentation my = this;

        if(checkMyThread == null){
            checkMyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!breakCheckThread){

                        if(checkCount >= 1000){
                            Logger.i(TAG,"checkMyInstrumentation break , checkCount = " + checkCount);
                            break;
                        }

                        try {
                            Object activityThreadObj = AndroidHackHelper.getActivityThread(AdClientContext.getClientContext());
                            Instrumentation instrumentation = AndroidHackHelper.getInstrumentation2(activityThreadObj);

                            Logger.i(TAG,"checkMyInstrumentation current instrumentation = " + instrumentation.getClass().getName());

                            if(instrumentation != null && !instrumentation.getClass().getName().startsWith("com.analytics.sdk")){
                                AndroidHackHelper.injectInstrumentation(activityThreadObj, my);
                                Logger.i(TAG,"checkMyInstrumentation inject my");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ++checkCount;

                        try {
                            Thread.sleep(3*100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    breakCheckThread = false;
                    checkMyThread = null;
                    checkCount = 0;

                    Logger.i(TAG,"** checkMyInstrumentation thread exit");

                }
            });

            checkMyThread.start();
        }
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        Logger.i(TAG,"hack application onCreate className = " + app.getClass().getName());
        mBase.callApplicationOnCreate(app);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        Logger.i(TAG,"hack activity onResume className = " + activity.getClass().getName());
        checkMyInstrumentation(activity);
        isl.getActivityMonitor().onCallActivityOnResumeMethodBefore(activity);
        try {
            mBase.callActivityOnResume(activity);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.mResumed = UIHelper.ActivityState.TRUE;
        }
        isl.getActivityMonitor().onCallActivityOnResumeMethodAfter(activity);
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        Logger.i(TAG,"hack activity onPause className = " + activity.getClass().getName());
        try {
            mBase.callActivityOnPause(activity);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.mResumed = UIHelper.ActivityState.TRUE;
        }
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        Logger.i(TAG,"hack activity onStop className = " + activity.getClass().getName());
        try {
            mBase.callActivityOnStop(activity);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.mStopped = UIHelper.ActivityState.TRUE;
        }
        IDynamicService dynamicService = ServiceManager.getService(IDynamicService.class);
        dynamicService.tryC();
    }


    @Override
    public void callActivityOnDestroy(final Activity activity) {
        Logger.i(TAG,"hack activity onDestory className = " + activity.getClass().getName());
        isl.getActivityMonitor().onCallActivityOnDestoryMethodBefore(activity);
        ActivityTaskManager.getInstance().popFromActivityStack(activity);
        try {
            mBase.callActivityOnDestroy(activity);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(HackPackageMapping.isSplashActivity(activity)) {
            splashActivityState.mDestroyed = UIHelper.ActivityState.TRUE;
        }
        isl.getActivityMonitor().onCallActivityOnDestoryMethodAfter(activity);
    }

    @Override
    public Context getContext() {
        return mBase.getContext();
    }

    @Override
    public Context getTargetContext() {
        return mBase.getTargetContext();
    }

    @Override
    public ComponentName getComponentName() {
        return mBase.getComponentName();
    }


}
