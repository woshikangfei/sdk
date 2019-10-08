package com.analytics.sdk.common.http.toolbox;


import com.analytics.sdk.common.http.NetworkResponse;
import com.analytics.sdk.common.http.Request;
import com.analytics.sdk.common.http.Response;

import java.util.Map;

public class ByteArrayRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    //create a static map for directly accessing headers
    public Map<String, String> responseHeaders;

    public ByteArrayRequest(String url,Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if(mListener != null){
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;
        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
