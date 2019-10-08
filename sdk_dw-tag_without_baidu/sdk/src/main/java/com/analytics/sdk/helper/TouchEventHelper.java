package com.analytics.sdk.helper;

import android.view.MotionEvent;
import android.view.View;

import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.service.ad.entity.ClickLoction;

/**
 * 帮助采集ClickLoction
 */
public class TouchEventHelper implements View.OnTouchListener{

    private ClickLoction clickLoction = new ClickLoction();
    private OnPerfromClickListener onPerfromClickListener = OnPerfromClickListener.EMPTY;

    public interface OnPerfromClickListener {
        OnPerfromClickListener EMPTY = new OnPerfromClickListener() {
            @Override
            public void onClick(View view, TouchEventHelper touchEventHelper) {

            }
        };
        void onClick(View view,TouchEventHelper touchEventHelper) ;
    }

    public static TouchEventHelper bind(View view, OnPerfromClickListener clickListener){
        if (view == null) {
            throw new AdSdkRuntimeException("bind view is null");
        }

        TouchEventHelper touchEventCollecter = new TouchEventHelper();
        if(clickListener != null){
            touchEventCollecter.onPerfromClickListener = clickListener;
        }
        view.setOnTouchListener(touchEventCollecter);

        return touchEventCollecter;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickLoction.setDownTime(System.currentTimeMillis());
                clickLoction.setDOWN_X((int)event.getX());
                clickLoction.setDOWN_Y((int)event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                clickLoction.setUpTime(System.currentTimeMillis());
                clickLoction.setUP_X((int)event.getX());
                clickLoction.setUP_Y((int)event.getY());
                performClick(v);
                break;
            default:
                break;
        }
        return true;
    }

    private void performClick(View v) {
        onPerfromClickListener.onClick(v,this);
    }

    public ClickLoction getClickLoction() {
        return clickLoction;
    }
}
