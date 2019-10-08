package com.analytics.sdk.dynamic.a;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;

public class UIHelper {

    static final String TAG = UIHelper.class.getSimpleName();

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

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


}
