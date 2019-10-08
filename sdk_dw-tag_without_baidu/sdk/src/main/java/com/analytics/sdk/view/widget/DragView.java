package com.analytics.sdk.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DragView extends View {

    private int lastX;
    private int lastY;
    private View targetView;
    private HitCallback hitCallback;

    public interface HitCallback{
        void onHit();
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTargetView(View view,HitCallback hitCallback){
        this.targetView = view;
        this.hitCallback = hitCallback;
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragView(Context context) {
        super(context);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                //计算移动的距离
                int offX = x - lastX;
                int offY = y - lastY;
                //调用layout方法来重新放置它的位置

                Rect targetRect = new Rect(getLeft()+offX, getTop()+offY,
                        getRight()+offX    , getBottom()+offY);

                layout(targetRect.left,targetRect.top,targetRect.right,targetRect.bottom);

                if(targetView != null){
                    Rect rect = new Rect();
                    targetView.getGlobalVisibleRect(rect);

                    if(rect.contains(targetRect)) {
                        hitCallback.onHit();
                        targetView = null;
                        hitCallback = null;
                    }

                }

                break;
        }

        return true;
    }
}
