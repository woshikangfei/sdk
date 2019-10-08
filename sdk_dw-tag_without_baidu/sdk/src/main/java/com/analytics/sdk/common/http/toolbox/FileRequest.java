package com.analytics.sdk.common.http.toolbox;


import com.analytics.sdk.common.http.NetworkResponse;
import com.analytics.sdk.common.http.Request;
import com.analytics.sdk.common.http.Response;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class FileRequest extends Request<byte[]> {
    private final Response.Listener<File> mListener;
    //create a static map for directly accessing headers
    public Map<String, String> responseHeaders;
    private String fileName;

    public FileRequest(String url,String fileName,Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        // this request would never use cache.
        this.fileName = fileName;
        setShouldCache(false);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(byte[] response) {

        final File file = new File(this.fileName);
        BufferedOutputStream output = null;
        InputStream input = null;

        if (response!=null) {

            try {

                //covert reponse to input stream
                input = new ByteArrayInputStream(response);
                output = new BufferedOutputStream(new FileOutputStream(file));

                byte data[] = new byte[1024];

                long total = 0;
                int count = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();

                if(mListener != null){
                    mListener.onResponse(file);
                }

            } catch(IOException e){
                e.printStackTrace();
            } finally {
                close(input);
                close(output);
            }
        }

    }

    private void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
