package com.adsdk.demo.splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adsdk.demo.DevMainActivity;
import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.LogControl;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.splash.SplashAdListener;
import com.qq.e.ads.nativ.ADSize;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity2 extends Activity {

    static final String TAG = SplashActivity2.class.getSimpleName();
    private boolean canJump = false;

    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_ID);

        Log.i(TAG,"onCreate enter");

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            loadAds();
        }

    }

    private void loadAds(){

        final ViewGroup linearLayout = this.findViewById(GlobalConfig.RConfig.SPLASH_ACTIVITY_LAYOUT_AD_ID);

        adRequest = new AdRequest.Builder(this)
                                .setCodeId(GlobalConfig.ChannelId.SPLASH)
                                .setAdContainer(linearLayout)
                                .setTimeoutMs(5*1000)
                                .build();

        adRequest.loadSplashAd(new SplashAdListener() {
            @Override
            public void onAdError(AdError adError) {
                LogControl.i(TAG,"onAdError enter , " + adError.toString());
            }

            @Override
            public void onAdClicked() {
                LogControl.i(TAG,"onAdClicked enter");
            }

            @Override
            public void onAdShow() {
                LogControl.i(TAG,"onAdShow enter");
            }

            @Override
            public void onAdExposure() {
                LogControl.i(TAG,"onAdExposure enter , tid = " + Thread.currentThread().getId());
            }

            @Override
            public void onAdDismissed() {
                LogControl.i(TAG,"onAdDismissed enter");
                next();
            }
        });

    }

    private void next() {
        if (canJump) {
            startActivity(new Intent(SplashActivity2.this,DevMainActivity.class));
            this.finish();
        } else {
            canJump = true;
        }
    }
    /**
     *
     * ----------非常重要----------
     *
     * Android6.0以上的权限适配简单示例：
     *
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     *
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

//        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            loadAds();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            loadAds();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume enter , " + adRequest.getAdRequestCount());
        if (canJump) {
            next();
        }
        canJump = true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause enter");
        canJump = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy enter");
        if(adRequest != null){
            adRequest.recycle();
            adRequest = null;
        }
    }

}
