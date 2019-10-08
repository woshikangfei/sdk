package com.analytics.sdk.common.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class UIHelper {

    static final String TAG = UIHelper.class.getSimpleName();

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * (double)density + 0.5D);
    }

    public static int getDenstiyDpi(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static boolean isFullScreen(Activity activity){
        if(activity == null){
            return false;
        }
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }
        return false;
    }

    public static ActivityState getActivityState() {
        throw new RuntimeException("not support");
    }

    public static String getScreenOrientationString(Context context){
        int orientation = getScreenOrientation(context);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return "L";
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return "P";
        }
        return "U";
    }

    public static boolean isActivityDestoryed(Context activity){
        if(activity!=null && activity instanceof Activity){

            Activity activityInstance = (Activity) activity;

            if(activityInstance.isFinishing()){
                return true;
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return activityInstance.isDestroyed();
            }

        }
        return false;
    }

    public static void showDialog(Activity activity, Dialog dlg) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                if(activity!=null && dlg!=null && !activity.isDestroyed() && !dlg.isShowing()){
                    dlg.show();
                }
            } else {
                if(dlg!=null && !dlg.isShowing()){
                    dlg.show();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void dismissDialog(Activity activity, Dialog dialog){

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                if(activity!=null && dialog!=null && !activity.isDestroyed() && dialog.isShowing()){
                    dialog.dismiss();
                }
            } else {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }



    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("getStatusBarHeight", result + "");
        return result;
    }


    public static class ActivityState {
        public static final int TRUE = 1;
        public static final int FALSE = 0;
        public static final int UNKNOW = -1;

        public long onCreateTime;
        public int mResumed;
        public int mStopped;
        public int mFinished;
        public int mDestroyed;

        public ActivityState(){
            reset();
        }

        public void reset(){
            onCreateTime = UNKNOW;
            mResumed = UNKNOW;
            mStopped = UNKNOW;
            mFinished = UNKNOW;
            mDestroyed = UNKNOW;
        }

    }

}
