package com.analytics.sdk.helper;

import android.content.Context;

import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public interface RequestListener {
        void onFinish(String response);

        void onError(String message);
    }

    public interface ImageRequestListener {
        void onError(String message);

        void onSuccess(InputStream stream);
    }

    static final String TAG = HttpUtils.class.getSimpleName();


    // 使用Get方法，path存储一个网址，Map存储一个键值对
    public static void sendHttpRequestForGet(final String path,
                                             final RequestListener listener,final Context activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.i(TAG,"sendHttpRequestForGet enter, url = " + path);
                HttpURLConnection connection = null;
                InputStream in=null;
                BufferedReader reader=null;
                try {
                    if (path == null){
                        if (listener != null) {
                            listener.onError("fail");
                        }
                        return;
                    }
                    StringBuilder response = new StringBuilder();
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    //Get请求不需要DoOutPut
                    //connection.setDoOutput(false);
                    //connection.setDoInput(true);
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setRequestProperty("User-Agent", DeviceHelper.getUA(activity));
                    Logger.i(TAG,"response code = " + connection.getResponseCode());
                    // 200表示连接成功
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        if (listener != null) {
                            Logger.i(TAG,"request finish");
                            listener.onFinish(response.toString());
                        }
                        //  System.out.println("xxxx1==="+response.toString());
                    }else if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP){
                        in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        if (listener != null) {
                            listener.onFinish(response.toString());
                            //LogUtils.log(response.toString());
                        }
                        //  System.out.println("xxxx2==="+response.toString());
                    } else{
                        if (listener != null) {
                            listener.onError("fail"+connection.getResponseCode());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.i(TAG,"request Exception = " + e.getMessage());
                    if (listener != null) {
                        listener.onError(e.getMessage());
                    }
                } finally {
                    if (in!=null){
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (reader!=null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    

    public static void getImage(final String imageUrl , final ImageRequestListener listener){

        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {
                Logger.i(TAG,"getImage enter, imageUrl = " + imageUrl);
                HttpURLConnection connection = null;
                InputStream stream=null;
                try {
                    // 如果获取失败，终止
                    if (PublicUtils.isEmpty(imageUrl)) {
                        if (listener != null) {
                            listener.onError("请求地址为空");
                        }
                        return;
                    }
                    // 请求地址
                    URL url = new URL(imageUrl);
                    // 获取连接
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(false);
                    connection.setDoInput(true);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    // 判断状态码
                    Logger.i(TAG,"response code = " + connection.getResponseCode());
                    if (connection.getResponseCode()  == HttpURLConnection.HTTP_OK) {
                        // 获得输入流
                        stream =connection.getInputStream();
                        if (listener != null) {
                            Logger.i(TAG,"request finish");
                            listener.onSuccess(stream);
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("fail"+connection.getResponseCode());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.i(TAG,"request Exception = " + e.getMessage());
                    if (listener != null) {
                        listener.onError(e.getMessage());
                    }
                }finally {
                    if (stream!=null){
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        });

    }
}
