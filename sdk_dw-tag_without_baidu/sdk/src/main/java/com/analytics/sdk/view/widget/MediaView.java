package com.analytics.sdk.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.qq.e.comm.util.GDTLogger;

/**
 */

public final class MediaView  extends FrameLayout {

    private int a;
    private int b;

    public MediaView(Context context) {
        super(context);
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public final void setRatio(int width, int height) {
        this.a = width;
        this.b = height;
    }

    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(this.a > 0 && this.b > 0) {
            int var3 = MeasureSpec.getSize(widthMeasureSpec);
            heightMeasureSpec = MeasureSpec.getSize(heightMeasureSpec);
            if(var3 * this.b >= this.a * heightMeasureSpec && heightMeasureSpec != 0) {
                if(var3 * this.b <= this.a * heightMeasureSpec && var3 != 0) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(var3, 1073741824);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, 1073741824);
                } else {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.a * heightMeasureSpec / this.b, 1073741824);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, 1073741824);
                }
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(var3, 1073741824);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(var3 * this.b / this.a, 1073741824);
            }
        } else {
            widthMeasureSpec = widthMeasureSpec;
            heightMeasureSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint({"NewApi"})
    protected final void onAttachedToWindow() {
        GDTLogger.d("onAttachedToWindow");
        super.onAttachedToWindow();
        if(Build.VERSION.SDK_INT >= 11 && !this.isHardwareAccelerated()) {
            GDTLogger.e("Hardware acceleration is off");
        }

    }
}
