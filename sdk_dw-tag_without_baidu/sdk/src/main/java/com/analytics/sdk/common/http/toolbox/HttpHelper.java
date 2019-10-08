package com.analytics.sdk.common.http.toolbox;

import android.content.Context;

import com.analytics.sdk.common.http.Request;
import com.analytics.sdk.common.http.RequestQueue;

public final class HttpHelper {

    static RequestQueue httpRequestQueue;

    public static void init(Context context){
        httpRequestQueue = Volley.newRequestQueue(context);
    }

    public static void send(Request request){
        httpRequestQueue.add(request);
    }

}
