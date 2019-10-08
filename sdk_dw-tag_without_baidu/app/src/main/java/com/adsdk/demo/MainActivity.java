package com.adsdk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.banner.BannerActivity;
import com.adsdk.demo.feedlist.FeedListActivity;
import com.adsdk.demo.interstitial.InterstitialActivity;
import com.adsdk.demo.pointdrawer.PointDrawerActivity;
import com.adsdk.demo.splash.SplashActivity2;
import com.adsdk.demo.video.RewardVideoActivity;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.debug.DebugReceiver;

public class MainActivity extends Activity implements View.OnClickListener{

    static final String TAG = MainActivity.class.getSimpleName();
    int FIRST = 1;
    int SECOND = 2;

    private int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    Button splash;
    Button banner;
    Button interstitial;
    Button video;
    Button feedlist;
    Button drawClickMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.MAIN_ACTIVITY_LAYOUT_ID);

        setInfos();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

        } else {
        }

//        try {
//            Method hookMethod = GDTLoggerHooker.class.getDeclaredMethod("hook",new Class[]{String.class});
//            Method backup = GDTLoggerHooker.class.getDeclaredMethod("backup",new Class[]{String.class});
//
//            Log.i(GDTLoggerHooker.TAG,"hookMethod = " + hookMethod + " , backup = " + backup);
//
//            HookMain.findAndBackupAndHook(GDTLogger.class,GDTLoggerHooker.methodName,GDTLoggerHooker.methodSig,hookMethod,backup);
//
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }

        if("com.meizu.media.ebook".equals(getPackageName())) {
            AdClientContext.init(this);
        }

        //sendBroadcast(new Intent(DebugReceiver.ACTION_OPEN_DEBUG_FLOAT_VIEW));

        initViews(SECOND);

    }

    private void setInfos() {
        TextView textView = this.findViewById(R.id.info);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("packageName:"+getPackageName()).append("\n");
//        stringBuilder.append("sdkVersion:"+AdConfig.getDefault().getSdkVersion()).append("\n");
//        stringBuilder.append("isPrintLog:"+AdConfig.getDefault().isPrintLog()).append("\n");
//        stringBuilder.append("splash:"+AdConfig.getDefault().getAd3rdSdkConfig().getSplashActivityName()).append("\n");
//        stringBuilder.append("main:"+AdConfig.getDefault().getAd3rdSdkConfig().getMainActivityName()).append("\n");
//        stringBuilder.append("releaseUrl:"+AdConfig.getDefault().getServerEnvConfig().getCurrentServerUrl()).append("\n");
//        stringBuilder.append("releaseUrl2:"+AdConfig.getDefault().getServerEnvConfig().getCurrentServerUrl2()).append("\n");
//        stringBuilder.append("imei:"+DeviceHelper.getImei(this)).append("\n");

        textView.setText(stringBuilder.toString());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
                initViews(FIRST);
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initViews(int number) {

        splash = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_SPLASH_ID);
        splash.setOnClickListener(this);

        banner = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_BANNER_ID);
        banner.setOnClickListener(this);

        interstitial = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_INTERSTITIAL_ID);
        interstitial.setOnClickListener(this);

        video = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_REWARD_VIDEO_ID);
        video.setOnClickListener(this);

        feedlist = this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_FEEDLIST_ID);
        feedlist.setOnClickListener(this);
        if (number != FIRST){
            splash.setText(splash.getText()+"(CodeId："+GlobalConfig.ChannelId.SPLASH+")");
            banner.setText(banner.getText()+"(CodeId："+GlobalConfig.ChannelId.BANNER+")");
            interstitial.setText(interstitial.getText()+"(CodeId："+GlobalConfig.ChannelId.INTERSTITIAL+")");
            video.setText(video.getText()+"(CodeId："+GlobalConfig.ChannelId.VIDEO+")");
            feedlist.setText(feedlist.getText()+"(CodeId："+GlobalConfig.ChannelId.FEED_LIST+")");
        }
        drawClickMap = this.findViewById(R.id.clickMap);
        drawClickMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_SPLASH_ID == id){
            startActivity(new Intent(MainActivity.this,SplashActivity2.class));
        } else if(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_BANNER_ID == id){
            startActivity(new Intent(MainActivity.this,BannerActivity.class));
        } else if(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_INTERSTITIAL_ID == id){
            startActivity(new Intent(MainActivity.this,InterstitialActivity.class));
        } else if(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_REWARD_VIDEO_ID == id){
            startActivity(new Intent(MainActivity.this,RewardVideoActivity.class));
        } else if(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_FEEDLIST_ID == id){
            startActivity(new Intent(MainActivity.this,FeedListActivity.class));
//            startActivity(new Intent(MainActivity.this,FeedListFillBannerActivity.class));
        } else if(R.id.clickMap == id) {
            startActivity(new Intent(MainActivity.this,PointDrawerActivity.class));
        }

    }


}
