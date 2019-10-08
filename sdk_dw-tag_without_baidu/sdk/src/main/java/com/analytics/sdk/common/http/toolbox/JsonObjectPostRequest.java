package com.analytics.sdk.common.http.toolbox;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.analytics.sdk.common.http.NetworkResponse;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.AuthFailureError;
import com.analytics.sdk.common.http.error.ParseError;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * A request for retrieving a {@link JSONObject} response body at a given URL, allowing for an
 * optional {@link JSONObject} to be passed in as part of the request body.
 */
public class JsonObjectPostRequest extends JsonRequest<String> {

    static boolean isEnableGzip = false;
    private String reqestBody;

    /**
     * Creates a new request.
     *
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null indicates no
     *     parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjectPostRequest(
            int method,
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<String> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(
                method,
                url,
                (jsonRequest == null) ? null : jsonRequest.toString(),
                listener,
                errorListener);

        reqestBody = (jsonRequest == null) ? null : jsonRequest.toString();

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        headers.put("Accept-Encoding", "gzip,deflate");
        headers.put("Content-Encoding", "gzip");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private int getShort(byte[] data)
    {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    private String getRealString(byte[] data)
    {
        byte[] h = new byte[2];
        h[0] = (data)[0];
        h[1] = (data)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in;
        StringBuilder sb = new StringBuilder();
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            if (t)
            {
                in = new GZIPInputStream(bis);
            }
            else
            {
                in = bis;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine())
            {
                sb.append(line);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public byte[] getBody() {
        if(isEnableGzip){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return compress(reqestBody);
            }
        }
        return super.getBody();
    }

    private boolean isEnableGzip(){
        return (isEnableGzip && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is <code>null
     * </code> , <code>POST</code> otherwise.
     *
     */
    public JsonObjectPostRequest(
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<String> listener,
            @Nullable Response.ErrorListener errorListener) {
        this(
                jsonRequest == null ? Method.GET : Method.POST,
                url,
                jsonRequest,
                listener,
                errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {

            if(isEnableGzip()){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    String result = getRealString(response.data);
                    return Response.success(
                            result, HttpHeaderParser.parseCacheHeaders(response));
                }
            }

            String jsonString =
                    new String(
                            response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            return Response.success(
                    jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private byte[] compress(String str) {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzip = new GZIPOutputStream(outStream)) {
                gzip.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return outStream.toByteArray();
        }catch (Exception E){
            return new byte[0];
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String uncompress(byte[] str){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str))) {
            int b;
            while ((b = gis.read()) != -1) {
                baos.write((byte) b);
            }
        }catch (Exception e){
            return "";
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }


}
