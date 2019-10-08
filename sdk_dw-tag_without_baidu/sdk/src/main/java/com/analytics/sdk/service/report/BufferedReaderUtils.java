package com.analytics.sdk.service.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kf on 2018/9/17.
 */

public class BufferedReaderUtils {


    /**
     * 返回request请求响应的数据
     * @param input
     * @return
     */
    private static String getRespData(InputStream input) {
        StringBuffer sb = new StringBuffer(300);
        try {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(input, "utf-8"));
            for (;;) {
                String str = bufferReader.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
            bufferReader.close();
            input.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }



    /**
     *
     * @param url
     * @param params 进过加密转换成16进制的参数
     * @param timeout
     * @return
     */
    public static String getData(String url, String params, int timeout) {
        //TODO:http连接池
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("content-type", "text/html");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            writeData(params, conn);
            String result = getRespData(conn.getInputStream());
            conn.disconnect();
            return result;
        } catch (Exception ex) {
            //throw new RuntimeException("post http request error : url=" + url, ex);
        }
        return "{}";
    }

    /**
     * 发起一个HTTP请求，返回解密的数据
     * @param url
     * @param params
     * @param timeout
     * @return
     */
    public static String getDatadecod(String url, String params, int timeout) {
        //根据一个URL ，post请求数据并返回response的数据
        String data = getData(url, params, timeout);
        if ((data == null) || (data.length() <= 0)) {
            return data;
        }
        return data;

    }


    /**
     * 构造URL
     * @param paramMap
     * @return
     */
    private static String buildUrlParams(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        try {

            Iterator iter = paramMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry localEntry = (Map.Entry) iter.next();
                if (sb.length() != 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode((String) localEntry.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(String.valueOf(localEntry.getValue()), "UTF-8"));
            }
        } catch (Exception ex) {
            throw new RuntimeException("encode params error", ex);
        }
        return sb.toString();
    }

    /**
     * 发送请求，将参数发送到服务uq
     * @param params
     * @param paramURLConnection
     */
    private static void writeData(String params, URLConnection paramURLConnection) {
        if (params == null) {
            return;
        }
        try {
            byte[] b = params.getBytes("utf-8");
            OutputStream out = paramURLConnection.getOutputStream();
            out.write(b);
            out.flush();
            out.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
