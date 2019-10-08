package com.analytics.sdk.view.strategy.click;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.reflect.ReflectHelper;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;

/**
 * 广点通误点策略, 当前类主要负责hook , 误点的实现由GDTInterstitialClickRandomStrategy完成
 */
public class GDTInterstitialHookStrategy {

    static final String TAG = GDTInterstitialHookStrategy.class.getSimpleName();

    int preDrawInvokeCount = 0;

    public void hook(UnifiedInterstitialAD unifiedInterstitialAD, final AdResponse adResponse) throws Exception {
        Logger.i(TAG,"hook enter");

        try {

            Object UIADIValue = ReflectHelper.getField(UnifiedInterstitialAD.class,unifiedInterstitialAD,"a");

            Object dialogValue = ReflectHelper.getField(UIADIValue.getClass(),UIADIValue,"f");

            if(dialogValue!=null && dialogValue instanceof Dialog){

                Dialog dialog = (Dialog) dialogValue;

                final View decorView = dialog.getWindow().getDecorView();

                Logger.i(TAG,"decorView width = " + decorView.getWidth() + " , height = " + decorView.getHeight());

                decorView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {

                        int width = decorView.getWidth();
                        int height = decorView.getHeight();

                        // FIXME: 2019/6/1 30次 在小米和Oppo两款手机上调试出来的值，可能并不准确
                        if(preDrawInvokeCount >= 50){
                            decorView.getViewTreeObserver().removeOnPreDrawListener(this);
                            return false;
                        }

                        Logger.i(TAG,"hook enter , interstitialView width = " + width + ", height = " + height + " , invoke count = " + preDrawInvokeCount);
                        if(width > 1 && height > 1){
                            if(decorView!=null && decorView instanceof FrameLayout){

                                FrameLayout frameLayout = (FrameLayout) decorView;

                                final Context context = frameLayout.getContext();
                                int dialogViewWidth = frameLayout.getWidth();

                                int paddingRight = UIHelper.dip2px(context,0);
                                int paddingTop = UIHelper.dip2px(context,0);
                                int closeBtnWidth = UIHelper.dip2px(context,40);
                                int closeBtnHeight = UIHelper.dip2px(context,40);
                                Rect rect = new Rect();
                                rect.set(dialogViewWidth - closeBtnWidth - paddingRight,paddingTop, dialogViewWidth - paddingRight,paddingTop + closeBtnHeight);

                                new GDTInterstitialClickRandomStrategy().apply(frameLayout,rect,adResponse);

                            }

                            decorView.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        }

                        ++preDrawInvokeCount;
                        return false;
                    }
                });

            }

            Logger.i(TAG,"hook exit");

        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"hook exception = " + e.getMessage());
            throw e;
        }

    }

}
