package com.analytics.sdk.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.helper.DateHelper;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.ISpamService;
import com.analytics.sdk.view.widget.SlideUnlockView;

public class SlideUnlockActivity extends Activity {

    static final String TAG = SlideUnlockActivity.class.getSimpleName();
    static final int MAX_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.jhsdk_activity_slide_unlock);

        final SlideUnlockView slideUnlockView = findViewById(R.id.slide_to_unlock);

        ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) slideUnlockView.getLayoutParams();

        int marginTop = SdkHelper.getRandom(0,AdClientContext.displayHeight - UIHelper.dip2px(this,280));
        mp.setMargins(0, marginTop, 0, 0);

        final Runnable timeoutTask = new Runnable() {
            @Override
            public void run() {
                ThreadExecutor.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
                ISpamService spamService = ServiceManager.getService(ISpamService.class);
                spamService.reportExcpIMEI(0);
            }
        };

        SlideUnlockView.CallBack callBack=new SlideUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {
            }

            @Override
            public void onUnlocked() {
                finish();
                ThreadExecutor.removeOnAndroidHandlerThread(timeoutTask);
                reportIMEI(1);
            }
        };
        slideUnlockView.setCallBack(callBack);

        ThreadExecutor.runOnAndroidHandlerThread(timeoutTask,15*1000);

    }

    @Override
    public void onBackPressed() {

    }

    private void reportIMEI(final int code){
        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {
                ISpamService spamService = ServiceManager.getService(ISpamService.class);
                spamService.reportExcpIMEI(code);
            }
        });
    }

    public static void start(Context context) {

        ISpamService spamService = ServiceManager.getService(ISpamService.class);
        DataProvider dataProvider = spamService.getDataProvider();
        String currentDate = DateHelper.currentDate();

        int currentCount = dataProvider.getInt(currentDate,0);

        if(currentCount >= MAX_COUNT){
            Logger.i(TAG,"start enter , current count = " + currentCount);
            return;
        }

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setClass(context,SlideUnlockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
