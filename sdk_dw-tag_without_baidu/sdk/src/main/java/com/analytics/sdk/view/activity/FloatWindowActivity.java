package com.analytics.sdk.view.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.view.widget.floatwin.FloatWindowManager;

public class FloatWindowActivity extends Activity {

    static final String TAG = "FloatWindowActivity";
    private static final int CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE = 100;

    public static void showFloatWindow(Context context){
        Intent intent = new Intent(context,FloatWindowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate enter");

        showSmallFloatWindow(FloatWindowActivity.this,true);

    }

    public void showSmallFloatWindow(Context context,boolean isShowOverlayPermission){
        Log.i(TAG,"showSmallFloatWindow enter , sdk_init = " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.i(TAG,"showSmallFloatWindow enter , #1");

            String floatConfig = CacheHelper.getHelper().getAsString("float_config");

            if(TextUtils.isEmpty(floatConfig)){
                new AlertDialog.Builder(this)
                        .setTitle("聚合SDK调试")
                        .setMessage("系统版本较低或部分OEM机型需要手动打开悬浮窗权限,如果点击开启后没有打开,则需要进入系统设置中手动开启!")
                        .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CacheHelper.getHelper().put("float_config","x",1 * 60 * 60);
                                FloatWindowManager.getInstance().showSmallFloatWin();
                                finish();
                            }
                        })
                        .setNegativeButton("不再提示", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CacheHelper.getHelper().put("float_config","x");
                                finish();
                            }
                        }).setCancelable(false).show();
            } else {
                FloatWindowManager.getInstance().showSmallFloatWin();
                finish();
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean canDrawOverlays = Settings.canDrawOverlays(context);
            Log.i(TAG,"showSmallFloatWindow enter , #2 canDrawOverlays = " + canDrawOverlays + " , isShowOverlayPermission = " + isShowOverlayPermission);
            if (canDrawOverlays) {
                FloatWindowManager.getInstance().showSmallFloatWin();
                finish();
                return;
            }
            if (isShowOverlayPermission) {

                new AlertDialog.Builder(this)
                        .setTitle("聚合SDK调试")
                        .setMessage("打开悬浮窗权限后,可随时观察聚合SDK相关运行时状态!")
                        .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent,CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();

                return;
            }
        }

        finish();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult enter");
        if (requestCode == CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE) {
            showSmallFloatWindow(this.getApplicationContext(), false);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy enter");
        super.onDestroy();
    }
}
