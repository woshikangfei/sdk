package com.analytics.sdk.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.R;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.ISpamService;
import com.analytics.sdk.view.widget.DragView;

public class DragActivity extends Activity {

    static final String TAG = DragActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jhsdk_spam_view);

        final View targetView = this.findViewById(R.id.target_view);

        DragView dragView = this.findViewById(R.id.drag_view);

        ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) dragView.getLayoutParams();

        int marginTop = SdkHelper.getRandom(0,300);
        mp.setMargins(0, marginTop, 0, 0);

        final Runnable timeoutTask = new Runnable() {
            @Override
            public void run() {

                Logger.i(TAG,"timeoutTask");

                ThreadExecutor.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });

                reportIMEI(0);

            }
        };

        dragView.setTargetView(targetView, new DragView.HitCallback() {
            @Override
            public void onHit() {
                ThreadExecutor.removeOnAndroidHandlerThread(timeoutTask);
                finish();
                reportIMEI(1);
            }
        });

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
        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setClass(context,DragActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
