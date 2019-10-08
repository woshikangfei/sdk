package com.adsdk.demo.pointdrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PointDrawer extends View {
    private static final String TAG = "PointDrawer";

    final List<Point> pointList = new ArrayList<>();
    Paint bluePaint = new Paint();

    public PointDrawer(Context context) {
        super(context);
        init();
    }

    public PointDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init(){
        bluePaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = pointList.size();
        int w = 5;
        int h = 5;
        if(size > 0){
            for (int i = 0; i < size; i++) {
                Point point = pointList.get(i);
                int fx = point.x;
                int fy = point.y;
                canvas.drawRect(fx,fy, fx + w,fy + h,bluePaint);
            }
        }

    }

    public void setPointList(List<Point> points) {
        pointList.clear();
        pointList.addAll(points);
        Log.i(TAG,"pointList size " + pointList.size());
        invalidate();
    }
}

