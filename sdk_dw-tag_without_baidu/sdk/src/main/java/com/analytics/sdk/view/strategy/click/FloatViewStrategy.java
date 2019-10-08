package com.analytics.sdk.view.strategy.click;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.R;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.ByteArrayRequest;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.common.view.gif.GifImageView;

/**
 * 悬浮的动画
 */
public class FloatViewStrategy {

    static final String TAG = FloatViewStrategy.class.getSimpleName();

    public void apply(Activity activity, ViewGroup floatViewParent){
        View floatView = activity.getLayoutInflater().inflate(R.layout.jhsdk_float_view,floatViewParent);

        final GifImageView imageView = floatView.findViewById(R.id.gifImageView);

        String gifUrl = "https://www.baidu.com/img/dong_7d4baac2f4dee0fab1938d2569f42034.gif";

        ByteArrayRequest byteArrayRequest = new ByteArrayRequest(gifUrl, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(final byte[] response) {

                Logger.i(TAG,"-------------onResponse enter = " + response.length);

                ThreadExecutor.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setBytes(response);
                        imageView.startAnimation();
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.i(TAG,"-------------onErrorResponse = " + error.getMessage());
            }
        });

        HttpHelper.send(byteArrayRequest);
    }

}
