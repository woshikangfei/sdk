package com.analytics.sdk.service.ad;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.config.CodeIdConfig;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.entity.AdResponse;

public class StrategyHelper {

    static final String TAG = StrategyHelper.class.getSimpleName();

    public static int BAIDU_AD_CONTAINER = 0;

    static int convert2IntRandom(float random){
        return (int) (random * 100);
    }

    public static boolean isHit(float random){

        int defaultRandom = SdkHelper.getRandom(0,99);
        boolean isDebugClickStrategy = AdConfig.getDefault().isDebugClickStrategy();
        int serverRandom = isDebugClickStrategy ? 100 : convert2IntRandom(random);

        Logger.i(TAG,"isHitBlack enter , defaultRandom = " + defaultRandom + " , serverRandom = " + serverRandom + " , serverFloatRandom = " + random);

        return (defaultRandom < serverRandom);
    }

    static ViewGroup applySplashStrategy(AdRequest adRequest){
        Logger.i(TAG,"apply start");
        Activity activity = adRequest.getActivity();
        ViewGroup adContainer = adRequest.getAdContainer();
        final View rootView = activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_gdt_and_csj_sdk_fill_layout2,adContainer);
        adContainer = rootView.findViewById(R.id.ad_root_layout);
        Logger.i(TAG,"apply end");
        return adContainer;
    }

    public static void onClickHit(AdResponse adResponse){
        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
        adStrategyService.onRandomClickHit(adResponse);
    }

    public static boolean isHit(AdRequest adRequest){
        CodeIdConfig adServerConfig = getAdServerConfigByCodeId(adRequest);

        Logger.i(TAG,"isHitBlack adServerConfig = " + adServerConfig);

        if(isHit(adServerConfig.getCr())) {
            return true;
        }
        return false;
    }

    public static boolean isHitClientClick(AdRequest adRequest){
        CodeIdConfig adServerConfig = getAdServerConfigByCodeId(adRequest);

        Logger.i(TAG,"isHitClientClick adServerConfig = " + adServerConfig);

        if(isHit(adServerConfig.getAr())) {
            return true;
        }
        return false;
    }

    public static boolean isHitCountdownStrategy(AdResponse adResponse){
        CodeIdConfig adServerConfig = getAdServerConfigByCodeId(adResponse.getClientRequest());

        Logger.i(TAG,"isHitCountdownStrategy adServerConfig = " + adServerConfig);

        return isHit(adServerConfig.getSr());
    }

    public static int getBlockMainActivityTime(AdResponse adResponse){
        CodeIdConfig adServerConfig = getAdServerConfigByCodeId(adResponse.getClientRequest());
        Logger.i(TAG,"getBlockMainActivityTime adServerConfig = " + adServerConfig);
        return adServerConfig.getDn();
    }

    public static boolean isHitBlockMainActivityStrategy(AdResponse adResponse){
        CodeIdConfig adServerConfig = getAdServerConfigByCodeId(adResponse.getClientRequest());

        Logger.i(TAG,"isHitBlockMainActivityStrategy adServerConfig = " + adServerConfig);

        return isHit(adServerConfig.getDr());
    }

    private static CodeIdConfig getAdServerConfigByCodeId(AdRequest adRequest) {
        String codeId = adRequest.getCodeId();
        IAdService adService = ServiceManager.getService(IAdService.class);
        return adService.getCodeIdConfig(codeId);
    }

    public static boolean sendSimulateEventWithStrategyService(ViewGroup viewGroup,AdResponse adResponse){
        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_RANDOM_CLICK_B,adResponse));
        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
        return adStrategyService.sendSimulateEvent(viewGroup);
    }

    public static boolean sendSimulateEventWithServerConfig(ViewGroup viewGroup,AdResponse adResponse){
        try {
            if(isHitCountdownStrategy(adResponse)) {
                return sendSimulateEventWithStrategyService(viewGroup,adResponse);
            }
        } catch (Exception e){
            e.printStackTrace(); //ignore
        }
        return false;
    }

    static boolean sendSimulateEvent(final ViewGroup adContainer){

        Logger.i(TAG,"sendTouchEvent enter");

        if(adContainer == null){
            return false;
        }

        long down = System.currentTimeMillis();

        int width = adContainer.getWidth();
        int height = adContainer.getHeight();

        int padding = 30;

        if(width < 2){
            width = UIHelper.getScreenWidth(adContainer.getContext());
        }

        if(height < 2){
            height = 90;
        }

        int evnetX = SdkHelper.getRandom(padding,width - padding);
        int eventY = SdkHelper.getRandom(height / 2,height - padding);

        Logger.i(TAG,"onClick , evnetX = " + evnetX + " , eventY = " + eventY);

        MotionEvent downEvent = MotionEvent.obtain(down, System.currentTimeMillis(), MotionEvent.ACTION_DOWN, evnetX, eventY, 0);
        final MotionEvent upEvent = MotionEvent.obtain(down, System.currentTimeMillis(), MotionEvent.ACTION_UP, evnetX, eventY, 0);

        adContainer.dispatchTouchEvent(downEvent);
        ThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adContainer.dispatchTouchEvent(upEvent);
                Logger.i(TAG,"dispatchTouchEvent success");
            }
        },10);

        return true;
    }

}
