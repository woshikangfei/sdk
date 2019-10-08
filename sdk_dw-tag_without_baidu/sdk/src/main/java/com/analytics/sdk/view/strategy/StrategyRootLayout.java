package com.analytics.sdk.view.strategy;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;
import com.analytics.sdk.view.strategy.click.ClickRandomDebugHelper;

public class StrategyRootLayout extends RelativeLayout {

    static final String TAG = StrategyRootLayout.class.getSimpleName();

    protected IAdStrategyService adStrategyService;
    protected AdStragegyWorkArgs adStragegyWorkArgs = new AdStragegyWorkArgs();
    private View rootView;

    public void setRootView(View rootView){
        this.rootView = rootView;
    }

    public StrategyRootLayout(Context context){
        super(context);
        init();
    }

    public StrategyRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        adStrategyService = ServiceManager.getService(IAdStrategyService.class);
    }

    public void setAdResponse(AdResponse adResponse){
        adStragegyWorkArgs.adResponse = adResponse;
    }

    public void setHitRect(Rect rect){
        Rect hitRect = new Rect();
        hitRect.set(rect.left,rect.top,rect.right,rect.bottom);
        adStragegyWorkArgs.hitRect = hitRect;
    }

    public void setViewSize(int width, int height){
        adStragegyWorkArgs.viewHeight = height;
        adStragegyWorkArgs.viewWidth = width;
    }

    public void setTouchEventRelocationable(AdStragegyWorkArgs.TouchEventRelocationable impl){
        adStragegyWorkArgs.touchEventRelocationImpl = (impl == null ? AdStragegyWorkArgs.TouchEventRelocationable.EMPTY : impl);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        adStragegyWorkArgs.event = event;

        Logger.i(TAG,"dispatchTouchEvent enter , action = " + SdkHelper.getMotionEventActionString(event));

        IAdStrategyService.CallResult callResult = adStrategyService.dispatchTouchEvent(adStragegyWorkArgs);
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

    public void apply(AdResponse adResponse){
        Context context = getContext();
        int paddingRight = UIHelper.dip2px(context,5);
        int paddingTop = UIHelper.dip2px(context,15);
        int closeBtnWidth = UIHelper.dip2px(context,85);
        int closeBtnHeight = UIHelper.dip2px(context,35);
        apply(this,adResponse,paddingTop,paddingRight,closeBtnWidth,closeBtnHeight);
    }

    public void apply(ViewGroup view,AdResponse adResponse,int paddingTop,int paddingRight,int closeBtnWidth,int closeBtnHeight){
        try {
            int rootViewWidth = view.getWidth();
            int rootViewHeight = view.getHeight();
            Rect closeRect = new Rect();
            closeRect.set(rootViewWidth - closeBtnWidth - paddingRight,paddingTop, rootViewWidth - paddingRight,paddingTop + closeBtnHeight);

            setAdResponse(adResponse);
            setViewSize(rootViewWidth,rootViewHeight);
            setHitRect(closeRect);

            if(AdConfig.getDefault().isPrintLog()){
                //仅仅是为了调试,策略是在dispatchTouchEvent中实现
                new ClickRandomDebugHelper().apply(view,closeRect,adResponse);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    static class DefaultTouchEventRelocationableImpl implements AdStragegyWorkArgs.TouchEventRelocationable {

        int margin = 30;
        int viewWidth = 0;
        int viewHeight = 0;

        public DefaultTouchEventRelocationableImpl(int viewWidth,int viewHeight) {
            this.viewHeight = viewHeight;
            this.viewWidth = viewWidth;
            this.margin = margin;
        }

        @Override
        public int getRelocationX() {
            return SdkHelper.getRandom(margin, viewWidth - margin);
        }

        @Override
        public int getRelocationY() {
            return SdkHelper.getRandom(viewHeight / 2, viewHeight - margin);
        }

    }

}
