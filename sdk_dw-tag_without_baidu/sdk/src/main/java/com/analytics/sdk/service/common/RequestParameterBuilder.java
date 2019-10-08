package com.analytics.sdk.service.common;

import android.content.Context;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.config.AdConfig;

import org.json.JSONException;
import org.json.JSONObject;

public final class RequestParameterBuilder {

    public static JSONObject buildJsonObjectParameters() throws JSONException {

        Context context = AdClientContext.getClientContext();

        JSONObject requestParams = new JSONObject();
        requestParams.put("sdk_version",AdConfig.getDefault().getSdkVersion());
        requestParams.put("app_package", context.getPackageName());
        requestParams.put("app_version", AppHelper.getVersionName(context));
        requestParams.put("device_id", DeviceHelper.getImei(context));

        return requestParams;

    }

    public static String buildJsonStringParameters() throws JSONException {
        return buildJsonObjectParameters().toString();
    }


}
