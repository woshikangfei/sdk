package com.analytics.sdk.view.widget.floatwin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;


import com.analytics.sdk.client.AdClientContext;

import static android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

/**
 * debug模式悬浮窗控制类
 */
public class FloatWindowManager implements SmallFloatWindowView.ISmallCallback, BigFloatWindowView.IBigCallback {
    private static final String SUB_TAG = "FloatWindowManager";
    private static FloatWindowManager instance;
    private WindowManager mWindowManager;
    private SmallFloatWindowView smallView;
    private BigFloatWindowView bigView;
    private WindowManager.LayoutParams smallParams;
    private WindowManager.LayoutParams bigParams;
    private DisplayMetrics dm;
    private int xPosition = 10;
    private int yPosition = 0;
    private int currentState = FloatWindowState.NON_WINDOW;
    public static final String SUB_FLOAT_WIN_RECEIVER_ACTION = "_float_win_receiver_action";

    private static class FloatWindowState {
        public static final int NON_WINDOW = 0;
        public static final int SMALL_WINDOW = 1;
        public static final int BIG_WINDOW = 2;
    }

    public static FloatWindowManager getInstance() {
        if (null == instance) {
            synchronized (FloatWindowManager.class) {
                if (null == instance) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    public FloatWindowManager() {
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public boolean isShown(){
        return (smallView != null || bigView != null);
    }

    /**
     * 显示小悬浮窗
     */
    public void showSmallFloatWin() {
        try {
            if (smallView == null) {
                smallView = new SmallFloatWindowView(AdClientContext.getClientContext(), dm.density);
                smallParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        FloatWindowUtils.getType(),
                        FLAG_NOT_FOCUSABLE | FLAG_ALT_FOCUSABLE_IM, PixelFormat.TRANSLUCENT);
                smallParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallParams.x = (int) xPosition;
                smallParams.y = (int) (yPosition);
                smallView.setWindowsParams(smallParams);
                smallView.setOnSmallCallback(this);
            }
            removeOldFloatWindow();
            getWindowManager().addView(smallView, smallParams);
            currentState = FloatWindowState.SMALL_WINDOW;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 显示大悬浮窗
     */
    public void showBigWindow() {
        if (bigView == null) {
            bigView = new BigFloatWindowView(AdClientContext.getClientContext(), dm.density);
            bigParams = new WindowManager.LayoutParams();
            bigParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    FloatWindowUtils.getType(),
                    FLAG_NOT_FOCUSABLE | FLAG_ALT_FOCUSABLE_IM, PixelFormat.TRANSLUCENT);
            bigParams.gravity = Gravity.LEFT | Gravity.TOP;
            bigParams.x = (int) xPosition;
            bigParams.y = (int) (dm.heightPixels / 4 - yPosition);
            bigView.setOnBigCallback(this);
        }
        removeOldFloatWindow();
        getWindowManager().addView(bigView, bigParams);
        bigView.updateData();
        currentState = FloatWindowState.BIG_WINDOW;
    }

    /**
     * 删除旧悬浮窗口
     */
    private void removeOldFloatWindow() {
        switch (currentState) {
            case FloatWindowState.SMALL_WINDOW:
                if (smallView == null) {
                    return;
                }
                getWindowManager().removeView(smallView);
                break;
            case FloatWindowState.BIG_WINDOW:
                if (bigView == null) {
                    return;
                }
                getWindowManager().removeView(bigView);
                break;
        }
    }

    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) AdClientContext.getClientContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 更新悬浮窗口数据
     */
    public void updateData(Intent intent) {
        Message msg = sHandler.obtainMessage();
        msg.obj = intent;
        sHandler.sendMessage(msg);
    }

    private Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (currentState == FloatWindowState.BIG_WINDOW) {
                Intent intent = (Intent) msg.obj;
                if (intent == null) {
                    return;
                }
                //更新大窗口数据
            }
        }
    };

    /**
     * 点击小悬浮窗口
     */
    @Override
    public void onSmallWindowClick() {
        showBigWindow();
    }

    /**
     * 点击大悬浮窗口
     */
    @Override
    public void onBigWindowClick() {
        showSmallFloatWin();
    }
}
