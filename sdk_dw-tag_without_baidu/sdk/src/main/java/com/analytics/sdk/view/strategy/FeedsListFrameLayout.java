package com.analytics.sdk.view.strategy;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.feedlist.AdViewExt;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.IAdStrategyServiceImpl;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;
import com.analytics.sdk.view.strategy.click.ClickRandomDebugHelper;
import com.analytics.sdk.view.strategy.click.InformationClickRandomStrategy;

import java.util.HashMap;

public class FeedsListFrameLayout extends FrameLayout {

    static final String TAG = FeedsListFrameLayout.class.getSimpleName();
    protected AdStragegyWorkArgs adStragegyWorkArgs = new AdStragegyWorkArgs();

    public FeedsListFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdViewExt adView;
    public int relocationDownX;
    public int relocationDownY;
    public  boolean isDownHit;
    public  boolean isHitStrategy = false;

    public AdResponse adResponse;

    public  int feedlistClickCount = 0;
    public  boolean canClick = true;
    public  boolean isFinished = false;

    private HashMap<String,Integer> adViewClickCountMap = new HashMap<>();

    public void setAdRequest(AdResponse adResponse) {
        this.adResponse = adResponse;
        adStragegyWorkArgs.adResponse = adResponse;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);

        adStragegyWorkArgs.event = event;
        adStragegyWorkArgs.feedsListFrameLayout = this;

        Logger.i(TAG,"dispatchTouchEvent enter , action = " + SdkHelper.getMotionEventActionString(event));

        IAdStrategyService.CallResult callResult = IAdStrategyService.CallResult.CALL_SUPER;
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
