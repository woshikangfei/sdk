package com.adsdk.demo.banner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.LogControl;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.banner.BannerAdListener;

public class BannerActivity extends AppCompatActivity {
    static final String TAG = "BannerActivity";
    FrameLayout frameLayout;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.BANNER_ACTIVITY_LAYOUT_ID);
        frameLayout=findViewById(GlobalConfig.RConfig.BANNER_ACTIVITY_AD_CONTAINER);

        adRequest = new AdRequest.Builder(this)
                        .setCodeId(GlobalConfig.ChannelId.BANNER)
                .setAdContainer(frameLayout)
                .build();

        adRequest.loadBannerAd(new BannerAdListener() {
                    @Override
                    public void onAdError(AdError adError) {
                        LogControl.i(TAG,"onAdError enter , message = " + adError.getErrorMessage());
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
                        LogControl.i(TAG,"onAdExposure enter");
                    }

                    @Override
                    public void onAdDismissed() {
                        LogControl.i(TAG,"onAdDismissed enter");
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        LogControl.i(TAG,"onResume enter");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogControl.i(TAG,"onStop enter");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogControl.i(TAG,"onPause enter");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adRequest!=null){
            adRequest.recycle();
        }
    }
}
