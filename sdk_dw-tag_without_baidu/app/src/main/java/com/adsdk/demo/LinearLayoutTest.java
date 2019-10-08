package com.adsdk.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.analytics.sdk.common.log.Logger;

public class LinearLayoutTest extends LinearLayout {

    static final String TAG = "LinearLayoutTest";

    public LinearLayoutTest(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
