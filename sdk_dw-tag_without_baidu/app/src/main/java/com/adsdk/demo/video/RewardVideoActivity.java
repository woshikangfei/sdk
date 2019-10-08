package com.adsdk.demo.video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.LogControl;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.video.RewardVideoAdListener;

public class RewardVideoActivity extends AppCompatActivity {

    static String TAG = RewardVideoActivity.class.getSimpleName();

    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.REWARD_VIDEO_ACTIVITY_LAYOUT_ID);
        loadAd();
    }

    private void loadAd() {

        adRequest = new AdRequest.Builder(this)
                .setCodeId(GlobalConfig.ChannelId.VIDEO)
                .setRewardName("金币")
                .setRewardAmount(100)
                .setUserID("user123")
                .build();

        adRequest.loadRewardVideoAd(new RewardVideoAdListener() {
                    @Override
                    public void onAdError(AdError adError) {
                        LogControl.i(TAG,"onAdError enter , msg = " + adError.getErrorMessage());
                        RewardVideoActivity.this.finish();
                    }

                    @Override
                    public void onAdClicked() {
                        LogControl.i(TAG,"onAdClicked enter");
                        RewardVideoActivity.this.finish();
                    }

                    @Override
                    public void onAdShow() {
                        LogControl.i(TAG,"onAdShow enter");
                    }

                    @Override
                    public void onAdVideoCompleted() {
                        LogControl.i(TAG,"onAdVideoCompleted enter");
                    }

                    @Override
                    public void onAdExposure() {
                        LogControl.i(TAG,"onAdExposure enter");
                    }

                    @Override
                    public void onAdDismissed() {
                        LogControl.i(TAG,"onAdDismissed enter");

                        if(adRequest != null && !adRequest.isRecycled()) {
                            adRequest.recycle();
                            adRequest = null;
                        }

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
