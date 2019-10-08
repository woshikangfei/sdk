package com.analytics.sdk.view.widget.floatwin;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.runtime.activity.ActivityTaskManager;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;
import com.analytics.sdk.config.AdConfig;

/**
 * 大悬浮窗视图
 */
public class BigFloatWindowView extends LinearLayout implements View.OnClickListener {
    private static final String SUB_TAG = "BigFloatWindowView";
    private Context mContext;

    private IBigCallback mCallback;
    private float mDipScale = 1;
    private TextView infoTextView;

    public BigFloatWindowView(Context context, float dipScale) {
        super(context);
        mDipScale = dipScale;
        mContext = context;
        init();
    }

    private void init() {
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(FloatWindowConfig.BIG_WINDOW_BG_COLOR);
        int padding = (int) (mDipScale * FloatWindowConfig.DEFAULT_PADDING);
        this.setPadding(padding, padding, padding, padding);
        this.setOnClickListener(this);
    }

    public void updateData(){
        getInfoView().setText("");

        Context context = AdClientContext.getClientContext();

        StringBuilder stringBuilder = new StringBuilder();

        String packageName = AppHelper.getAppPackageName(context);
        stringBuilder.append("packageName=").append(packageName).append("\n");
        String versionName = AppHelper.getVersionName(context);
        stringBuilder.append("versionName=").append(versionName).append("\n");
        String phoneModel = Build.MODEL;
        stringBuilder.append("phoneModel=").append(phoneModel).append("\n");
        String osVersion = DeviceHelper.getOsVersion();
        stringBuilder.append("osVersion=").append(osVersion).append("\n");
        stringBuilder.append("imei=").append(DeviceHelper.getImei(context)).append("\n");
        stringBuilder.append("screen=").append(AdClientContext.displayWidth+"x"+AdClientContext.displayHeight).append(",dpi=").append(UIHelper.getDenstiyDpi(context)).append("\n");
        stringBuilder.append("isRooted=").append(DeviceHelper.isRootedDevice()).append("\n");
        boolean isGrantReadPhoneStatePermission = PermissionsHelper.isGrantReadPhoneStatePermission(context);
        boolean isGrantWriteExternalStoragePermission = PermissionsHelper.isGrantWriteExternalStoragePermission(context);
        stringBuilder.append("permission phone=").append(isGrantReadPhoneStatePermission).append(",").append("extStorage=").append(isGrantWriteExternalStoragePermission).append("\n");
        stringBuilder.append("UA=").append(DeviceHelper.getUA(context)).append("\n");

        Activity activity = ActivityTaskManager.getInstance().peekTopActivity();
        if(activity != null){
            String topClassName = activity.getClass().getName();
            stringBuilder.append("topActivity=").append(topClassName).append("\n");
        }
        stringBuilder.append("\n");
        String coreString = AdConfig.getDefault().toCoreString();
        stringBuilder.append(coreString).append("\n");
        getInfoView().setText(stringBuilder);
    }

    private TextView getInfoView() {
        if(infoTextView == null){
            LinearLayout container = new LinearLayout(mContext);
            container.setOrientation(VERTICAL);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) (mDipScale * 10), 0, 0);
            infoTextView = new TextView(mContext);
            infoTextView.setTextColor(FloatWindowConfig.TEXT_COLOR_GREEN);
            infoTextView.setTextSize(FloatWindowConfig.DEFAULT_TEXT_SIZE);
            container.addView(infoTextView);
            this.addView(container, params);
        }
        return infoTextView;
    }

    public void setOnBigCallback(IBigCallback bigCallback) {
        mCallback = bigCallback;
    }

    @Override
    public void onClick(View v) {
        mCallback.onBigWindowClick();
    }

    public interface IBigCallback {
        void onBigWindowClick();
    }
}
