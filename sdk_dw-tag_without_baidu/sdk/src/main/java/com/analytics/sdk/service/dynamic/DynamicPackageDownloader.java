package com.analytics.sdk.service.dynamic;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.http.DefaultRetryPolicy;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.FileRequest;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.network.ConnectivityListener;
import com.analytics.sdk.common.network.ConnectivityMonitor;
import com.analytics.sdk.common.network.NetworkHelper;

import java.io.File;

/**
 * 补丁包下载
 */
public class DynamicPackageDownloader {

    static final String TAG = DynamicPackageDownloader.class.getSimpleName();

    private OnDownloadListener onDownloadSuccessListener;
    private ConnectivityMonitor connectivityMonitor;

    public interface OnDownloadListener {
        OnDownloadListener EMPTY = new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
            }

            @Override
            public void onDownloadError() {

            }
        };
        void onDownloadSuccess(File file);
        void onDownloadError();
    }

    public DynamicPackageDownloader() {
    }

    public void start(final String downloadUrl, final String fileName, final OnDownloadListener onDownloadSuccessListener){

        this.onDownloadSuccessListener = (onDownloadSuccessListener == null ? OnDownloadListener.EMPTY : onDownloadSuccessListener);

        if(NetworkHelper.isNetworkAvailable(AdClientContext.getClientContext())) {
            startDownloadFromServer(downloadUrl, fileName, onDownloadSuccessListener);
        } else {

            this.connectivityMonitor = ConnectivityMonitor.startNewMonitor(AdClientContext.getClientContext(), new ConnectivityListener() {
                @Override
                public void onConnectivityChanged(boolean isConnected) {
                    if(isConnected){
                        startDownloadFromServer(downloadUrl, fileName, onDownloadSuccessListener);
                    }
                }
            });

        }

    }

    private void startDownloadFromServer(String downloadUrl, String fileName, final OnDownloadListener onDownloadSuccessListener) {
        Logger.i(TAG,"startDownloadFromServer enter");
        FileRequest fileRequest = new FileRequest(downloadUrl, fileName, new Response.Listener<File>() {
            @Override
            public void onResponse(File response) {
                Logger.i(TAG,"start onResponse , file exists = " + response.exists() + " , response = " + response.getAbsolutePath());

                onDownloadSuccessListener.onDownloadSuccess(response);

//                ReportData.obtain("hotfix_download_success",AdEventActions.ACTION_AD_HOTFIX).startReport();

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.i(TAG,"start onErrorResponse , error = " + error.getMessage());
//                ReportData.obtain("hotfix_download_error",AdEventActions.ACTION_AD_HOTFIX).startReport();
                onDownloadSuccessListener.onDownloadError();
            }
        });

        fileRequest.setShouldRetryServerErrors(true);
        fileRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HttpHelper.send(fileRequest);
    }

}
