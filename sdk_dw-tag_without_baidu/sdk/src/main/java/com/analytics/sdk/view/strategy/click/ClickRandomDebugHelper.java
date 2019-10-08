package com.analytics.sdk.view.strategy.click;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.ClickMap;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.service.ad.IAdStrategyServiceImpl;
import com.analytics.sdk.service.ad.entity.AdResponse;

import java.util.List;

/**
 * 参考穿山甲 com.bytedance.sdk.openadsdk.g.j
 * 广点通横幅误点
 */
public class ClickRandomDebugHelper {

    static final String TAG = "ClickRandomDebugHelper";

    private Rect closeRectArea = new Rect();

    public static View debugView;

    public static View createTopView(final Rect hitRect,final View targetView,final AdResponse adResponse){
        final Context context = targetView.getContext();

        debugView = new DebugViewer(context,targetView,adResponse,hitRect,targetView.getWidth(),targetView.getHeight(),0);
        debugView.setLayoutParams(new ViewGroup.LayoutParams(targetView.getWidth(),targetView.getHeight()));
        debugView.setBackgroundColor(Color.TRANSPARENT);

        return debugView;
    }

    public static void invalidateDebugView() {
        if(debugView != null){
            debugView.invalidate();
        }
    }

    public void apply(ViewGroup targetView, Rect rect, AdResponse adResponse) {

        closeRectArea.set(rect);

        Logger.i(TAG,"apply , view width = " + targetView.getWidth() + " , height = " + targetView.getHeight());
        View topView = createTopView(closeRectArea,targetView,adResponse);
        topView.setTag("debug");
        targetView.addView(topView);

    }

    public static class DebugViewer extends View {
        Paint redAlphaPaint = new Paint();
        Paint blackAlphaPaint = new Paint();
        Paint bluePaint = new Paint();
        Paint redPaint2 = new Paint();
        Paint blackPaint = new Paint();
        Paint blackPaintTextSizeSmall = new Paint();
        Paint cellPaint = new Paint();
        AdResponse adResponse = null;
        Rect hitRect = new Rect();
        View targetView;
        int viewWidth;
        int viewHeight;
        int cellMarginTop = 0;

        public DebugViewer(Context context,View targetView, AdResponse adResponse,Rect hitRect,int viewWidth,int viewHeight,int cellMarginTop){
            super(context);
            this.adResponse = adResponse;
            this.hitRect = hitRect;
            this.targetView = targetView;
            this.viewWidth = viewWidth;
            this.viewHeight = viewHeight;
            this.cellMarginTop = cellMarginTop;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(AdConfig.getDefault().isPrintLog()){
                final IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
                blackAlphaPaint.setColor(UIHelper.adjustAlpha(Color.BLACK,0.3f));
                redAlphaPaint.setColor(UIHelper.adjustAlpha(Color.RED,0.3f));
                redPaint2.setColor(Color.RED);

                redPaint2.setStrokeWidth((float) 5.0);
                redPaint2.setStyle(Paint.Style.STROKE);

                blackPaint.setColor(Color.BLACK);
                blackPaint.setTextSize(UIHelper.dip2px(getContext(),25));

                blackPaintTextSizeSmall.setColor(Color.BLACK);
                blackPaintTextSizeSmall.setTextSize(UIHelper.dip2px(getContext(),11));

                if(AdConfig.getDefault().isDrawCells()){

                    ClickMap clickMap = adStrategyService.getClickMap(adResponse.getClientRequest());

                    if(clickMap != null && clickMap.isRealy()) {

                        int rowCellSize = clickMap.getRowCellSize();
                        int columnCellSize = clickMap.getColumnCellSize();

                        int cellWidth = viewWidth / rowCellSize;
                        int cellHeight = viewHeight / columnCellSize;
                        int[][] cellValueArray = clickMap.getCellValueArray();
                        int[] colorArray = clickMap.getColorArray();

                        for(int i = 0;i < rowCellSize;i++){

                            for(int j = 0;j < columnCellSize;j++){

                                int left = j * cellWidth;
                                int top = i * cellHeight + cellMarginTop;
                                int right = (j+1) * cellWidth;
                                int bottom = (i+1) * cellHeight + cellMarginTop;

                                Rect rect = new Rect(left,top,right,bottom);

                                int value = 0;

                                if(cellValueArray != null){
                                    value = cellValueArray[i][j];
                                }

                                if(i % 2 == 0){
                                    int color = colorArray[j];
                                    cellPaint.setColor(color);
                                    canvas.drawRect(rect,cellPaint);
                                    if(AdConfig.getDefault().isDrawCellValue()) {
                                        canvas.drawText(""+value,rect.left,rect.bottom,blackPaintTextSizeSmall);
                                    }
                                    if(i == 0){
//                                    canvas.drawText(""+j,rect.left,rect.bottom,blackPaint);
                                    }

                                } else {
                                    int color = colorArray[colorArray.length - j - 1];
                                    cellPaint.setColor(color);
                                    canvas.drawRect(rect,cellPaint);
                                    if(AdConfig.getDefault().isDrawCellValue()) {
                                        canvas.drawText(""+value,rect.left,rect.bottom,blackPaintTextSizeSmall);
                                    }
                                }

                                if(j == 0){
//                                canvas.drawText(""+i,rect.left,rect.bottom,blackPaint);
                                }

                            }
                        }

                        if(IAdStrategyServiceImpl.selectRect != null){
                            canvas.drawRect(IAdStrategyServiceImpl.selectRect,blackPaint);
                        }

                        List<Point> pointList = IAdStrategyServiceImpl.pointArray;
                        int psize = pointList.size();
                        int w = 5;
                        int h = 5;
                        for (int i = 0;i < psize;i++){
                            Point point = pointList.get(i);

                            int relocationDownX = point.x;
                            int relocationDownY = point.y + cellMarginTop;

                            canvas.drawRect(new Rect(relocationDownX,relocationDownY,relocationDownX+w,relocationDownY+h),blackPaint);
                        }

                    }

                }

                canvas.drawRect(hitRect, redAlphaPaint);

                if(IAdStrategyServiceImpl.noSavePointRect != null){
                    canvas.drawRect(IAdStrategyServiceImpl.noSavePointRect, blackAlphaPaint);
                }

                Point cPoint = IAdStrategyServiceImpl.lastClickPoint;
                if(cPoint != null){
                    canvas.drawCircle(cPoint.x,cPoint.y,IAdStrategyServiceImpl.C_RAD,redPaint2);
                    int relocationDownX = cPoint.x;
                    int relocationDownY = cPoint.y;
                    canvas.drawRect(new Rect(relocationDownX,relocationDownY,relocationDownX+2,relocationDownY+2),redPaint2);
                }

            }


        }
    }

}
