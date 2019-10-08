package com.analytics.sdk.view.strategy;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;

/**
 * 目前是飞读使用
 */
public class FeedsListFrameLayout2 extends FrameLayout {

    static final String TAG = FeedsListFrameLayout2.class.getSimpleName();
    protected AdStragegyWorkArgs adStragegyWorkArgs = new AdStragegyWorkArgs();

    public FeedsListFrameLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdResponse adResponse;
    public boolean isDownHit;
    public View adView;
    public boolean canClick = true;

    public void setAdRequest(AdResponse adResponse) {
        this.adResponse = adResponse;
        adStragegyWorkArgs.adResponse = adResponse;
    }

    public void setRect(Rect rect) {
        adStragegyWorkArgs.hitRect = rect;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);

        adStragegyWorkArgs.event = event;
        adStragegyWorkArgs.feedsListFrameLayout2 = this;

        Logger.i(TAG,"dispatchTouchEvent enter , action = " + SdkHelper.getMotionEventActionString(event));

        IAdStrategyService.CallResult callResult = adStrategyService.dispatchTouchEventWithFeedlist2(adStragegyWorkArgs);
        if(IAdStrategyService.CallResult.CALL_RECURSION == callResult) {
            return dispatchTouchEvent(adStragegyWorkArgs.event);
        } else if(IAdStrategyService.CallResult.CALL_SUPER == callResult) {
            return super.dispatchTouchEvent(adStragegyWorkArgs.event);
        } else if(IAdStrategyService.CallResult.CALL_RETURN_TRUE == callResult) {
            return true;
        } else {
            return super.dispatchTouchEvent(adStragegyWorkArgs.event);
        }

    }

}
