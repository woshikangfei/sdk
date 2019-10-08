package com.analytics.sdk.service.ad.entity;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.view.strategy.FeedsListFrameLayout;
import com.analytics.sdk.view.strategy.FeedsListFrameLayout2;

public class AdStragegyWorkArgs {

    public MotionEvent event;
    /**
     * 关闭按钮的区域
     */
    public Rect hitRect = new Rect();
    /**
     * view的宽度(有可能是广点通 | 穿山甲)
     * 这个宽度通常是误点区域的parent的宽度
     */
    public int viewWidth = 0;
    /**
     * 当前view的高度(有可能是广点通 | 穿山甲)
     * 这个宽度通常是误点区域的parent的宽度
     */
    public int viewHeight = 0;

    public AdResponse adResponse;

    public TouchEventRelocationable touchEventRelocationImpl = TouchEventRelocationable.EMPTY;

    public FeedsListFrameLayout feedsListFrameLayout;
    public FeedsListFrameLayout2 feedsListFrameLayout2;

    public boolean hasTouchEventRelocationable(){
        if(touchEventRelocationImpl != null){
            int rx = touchEventRelocationImpl.getRelocationX();
            int ry = touchEventRelocationImpl.getRelocationY();

            if(rx >= 0 && ry >= 0){
                return true;
            }

        }
        return false;
    }

    public interface TouchEventRelocationable {

        int getRelocationX();
        int getRelocationY();

        TouchEventRelocationable EMPTY = new TouchEventRelocationable() {
            @Override
            public int getRelocationX() {
                return -1;
            }

            @Override
            public int getRelocationY() {
                return -1;
            }

        };

    }

}
