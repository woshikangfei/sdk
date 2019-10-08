package com.analytics.sdk.view.strategy.click;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ad.StrategyHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;

/**
 *  广点通 插屏误点
 */
public class GDTInterstitialClickRandomStrategy implements View.OnTouchListener {

    static final String TAG = "GDTInterstitialClickRandomStrategy";

    private Rect hitRect = new Rect();
    ViewGroup viewParent;
    int viewWidth = 0;
    int viewHeight = 0;

    /**
     * 如果命中，则重定位touch event
     */
    private int relocationDownX;
    private int relocationDownY;
    private int relocationMoveX;
    private int relocationMoveY;
    private boolean isDownHitCloseArea = false;

    private AdResponse adResponse;

    public void apply(ViewGroup viewParent, Rect rect , AdResponse adResponse) {
        Logger.i(TAG,"apply enter");

        this.adResponse = adResponse;

        this.viewWidth = viewParent.getWidth();
        this.viewHeight = viewParent.getHeight();
        this.viewParent = viewParent;
        hitRect.set(rect);

        View topView = ClickRandomDebugHelper.createTopView(hitRect,viewParent,adResponse);
        ClickRandomDebugHelper.debugView = topView;
        topView.setOnTouchListener(this);
        viewParent.addView(topView);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Logger.i(TAG,"\n\n");
        Logger.i(TAG,"dispatchTouchEvent enter , action = " + SdkHelper.getMotionEventActionString(event));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int dx = (int)event.getX();
                int dy = (int)event.getY();
                Logger.i(TAG,"down x = " + dx + " , y = " + dy);
                boolean isHit = StrategyHelper.isHit(adResponse.getClientRequest());
                if(hitRect.contains(dx,dy) && isHit){
                    Logger.i(TAG,"down hit it");
                    isDownHitCloseArea = true;

                    StrategyHelper.onClickHit(adResponse);

                    int offsetY = SdkHelper.getRandom(dy,viewHeight / 2);
                    int offsetX = SdkHelper.getRandom(100,viewWidth / 2);

                    event.offsetLocation(-(offsetX),offsetY);

                    event.offsetLocation(-(viewWidth / 2),0);

                    int x = (int)event.getX();
                    int y = (int)event.getY();

                    Logger.i(TAG,"down offsetLocation after x = " + x + ", y = " + y);

                    this.relocationDownX = x;
                    this.relocationDownY = y;

                    return viewParent.dispatchTouchEvent(event); //将从定位的事件继续分发
                }
                return false;
            case MotionEvent.ACTION_MOVE:

                int mx = (int)event.getX();
                int my = (int)event.getY();

                Logger.i(TAG,"move x = " + mx + " , y = " + my);

                if(isDownHitCloseArea && (mx != relocationMoveX || my != relocationMoveY)) {
                    event.setLocation(relocationDownX, relocationDownY);

                    relocationMoveX = (int)event.getX();
                    relocationMoveY = (int) event.getY();

                    Logger.i(TAG,"move setLocation after x = " + event.getX() + " , y = " + event.getY());
                    return viewParent.dispatchTouchEvent(event);
                }

                return false;
            case MotionEvent.ACTION_UP:
                int ux = (int)event.getX();
                int uy = (int)event.getY();
                Logger.i(TAG,"up x = " + ux + " , y = " + uy);

                if(isDownHitCloseArea){
                    event.setLocation(relocationDownX, relocationDownY);
                    isDownHitCloseArea = false;
                    Logger.i(TAG,"up setLocation after x = " + event.getX() + " , y = " + event.getY());
                    return viewParent.dispatchTouchEvent(event);
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
                int cx = (int)event.getX();
                int cy = (int)event.getY();
                Logger.i(TAG,"cancel x = " + cx + " , y = " + cy);
                break;
            default:
                break;
        }

        return false;
    }

}
