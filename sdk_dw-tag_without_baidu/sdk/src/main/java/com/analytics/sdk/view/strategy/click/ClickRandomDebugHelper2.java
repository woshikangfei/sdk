package com.analytics.sdk.view.strategy.click;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.ad.IAdStrategyServiceImpl;
import com.analytics.sdk.service.ad.entity.AdResponse;

/**
 * 参考穿山甲   com.bytedance.sdk.openadsdk.g.j
 * 广点通横幅误点
 */
public class ClickRandomDebugHelper2 {

    static final String TAG = "ClickRandomDebugHelper2";

    public static Rect closeRectArea = new Rect();
    public static View debugView;

    public static View createTopView(final Rect hitRect,final View targetView,final AdResponse adResponse){
        final Context context = targetView.getContext();

        debugView = new DebugViewer2(context,targetView,adResponse,hitRect);
        debugView.setLayoutParams(new ViewGroup.LayoutParams(targetView.getWidth(),targetView.getHeight()));
//        debugView.setBackgroundColor(Color.TRANSPARENT);
        debugView.setBackgroundColor(UIHelper.adjustAlpha(Color.RED,0.3f));
        return debugView;
    }

    public void apply(ViewGroup targetView, Rect rect, AdResponse adResponse) {

        closeRectArea.set(rect);

        Logger.i(TAG,"apply , view width = " + targetView.getWidth() + " , height = " + targetView.getHeight());
        View topView = createTopView(closeRectArea,targetView,adResponse);
        topView.setTag("debug");
        targetView.addView(topView);

    }

    public static void setRect(Rect rect){
        closeRectArea = rect;
    }

    public static class DebugViewer2 extends ClickRandomDebugHelper.DebugViewer{
        Paint bluePaint = new Paint();
        Paint blackAlphaPaint = new Paint();
        Rect hitRect;

        public DebugViewer2(Context context, View targetView, AdResponse adResponse, Rect hitRect) {
            super(context, targetView, adResponse, hitRect,hitRect.width(),hitRect.height(),hitRect.top);
            this.hitRect = hitRect;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(AdConfig.getDefault().isPrintLog()) {

                bluePaint.setColor(Color.BLUE);
                blackAlphaPaint.setColor(UIHelper.adjustAlpha(Color.BLACK,0.3f));
                canvas.drawRect(hitRect, blackAlphaPaint);

                int relocationDownX = IAdStrategyServiceImpl.relocationDownX;
                int relocationDownY = IAdStrategyServiceImpl.relocationDownY + hitRect.top;
                canvas.drawRect(new Rect(relocationDownX,relocationDownY,relocationDownX+10,relocationDownY+10),bluePaint);

            }

        }
    }

}
