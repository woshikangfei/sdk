package com.analytics.sdk.service.report;

import android.content.Context;
import android.text.TextUtils;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.http.toolbox.JsonArrayPostRequest;
import com.analytics.sdk.common.http.toolbox.JsonObjectPostRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.network.ConnectivityListener;
import com.analytics.sdk.common.network.ConnectivityMonitor;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.runtime.broadcast.LocalBroadcastManager;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.report.entity.ReportData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class IReportServiceImpl extends AbstractService implements IReportService {

    private DataProvider reportDataProvider;
    private ConnectivityMonitor connectivityMonitor;
    static final int BATCH_REPORT_MAX_COUNT = 5;

    public IReportServiceImpl() {
        super(IReportService.class);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        reportDataProvider = DataProvider.newProvider(AdClientContext.getClientContext(),"report_database");
        connectivityMonitor = ConnectivityMonitor.startNewMonitor(context, new ConnectivityListener() {
            @Override
            public void onConnectivityChanged(boolean isConnected) {

                if(isConnected){
                    startBatchReportLocal();
                } else {
                    //如果这时断网？ 如何实现？
                }
            }
        });
        log(IReportService.class,"init success");
    }

    @Override
    public void destory() {
        super.destory();
        if(connectivityMonitor != null){
            connectivityMonitor.stop();
            connectivityMonitor = null;
        }
    }

    @Override
    public boolean report(final ReportData reportData, Listener<String,String> listener) {

        log(IReportServiceImpl.class,"report enter");
        final Listener<String,String> clientListener = (listener == null ? Listener.EMPTY : listener);

        final JSONObject reportJsonData = reportData.buildReportJson();

        startReport(reportData.getReportId(), clientListener, reportJsonData);

        return true;
    }

    private void startReport(final String reportId, final Listener<String, String> clientListener, final JSONObject jsonObject) {
        String requestUrl = AdConfig.getDefault().getServerEnvConfig().getLogUrlV2();

        final String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes();
        log(IReportServiceImpl.class,"bytes len = " + bytes.length);

        Logger.printJson(jsonString,"startReportV2("+requestUrl+") json data ↓");
        printKeyData(jsonObject);

        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, jsonObject, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                log(IReportServiceImpl.class,"report.onResponse enter");
                clientListener.onSuccess(Listener.SuccessMessage.obtain(reportId,response));

                startBatchReportLocal();

                LocalBroadcastManager.sendBroadcast(IReportService.ACTION_REPORT_SUCCESS,jsonString);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                final String errorType = clientListener.errorNotifier(error);
                log(IReportServiceImpl.class,"report.onErrorResponse enter , errorType = " + errorType);

                save2Local(jsonObject);

                LocalBroadcastManager.sendBroadcast(IReportService.ACTION_REPORT_ERROR,jsonString);

            }
        });

//        jsonObjectPostRequest.setShouldRetryServerErrors(true);
//        jsonObjectPostRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        HttpHelper.send(jsonObjectPostRequest);
    }

    private void printKeyData(JSONObject jsonObject) {

        StringBuilder stringBuilder = new StringBuilder();
        try {

            if(jsonObject.has("apiOrSdkAdType")){
                stringBuilder.append("source = ").append(jsonObject.getString("apiOrSdkAdType")).append(",");
            } else if(jsonObject.has("action")){
                stringBuilder.append("action = ").append(jsonObject.getString("action")).append(",");
            } else if(jsonObject.has("channel")){
                stringBuilder.append("codeId = ").append(jsonObject.getString("channel")).append(",");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.i("report_impl",stringBuilder.toString());
    }

    private volatile boolean isBatchReport = false;

    @Override
    public boolean startBatchReportLocal(){
        log(IReportServiceImpl.class,"startBatchReportLocal enter , isBatchReport = " + isBatchReport);
        if(isBatchReport){
           return false;
        }

        isBatchReport = true;
        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {


                String requestUrl = AdConfig.getDefault().getServerEnvConfig().getLogUrlV3();

                final Map<String,Object> cacheDataMap = reportDataProvider.all();
                final Map<String,Object> removeDataMap = new HashMap<>();

                if(cacheDataMap == null || cacheDataMap.size() == 0){
                    isBatchReport = false;
                    log(IReportServiceImpl.class,"startBatchReportLocal local report size is zero");
                    return;
                }

                final JSONArray jsonArray = new JSONArray();

                int fori = 0;

                log(IReportServiceImpl.class,"startBatchReportLocal all size = " + cacheDataMap.size());

                for(Iterator<Map.Entry<String,Object>> iter = cacheDataMap.entrySet().iterator();iter.hasNext();){

                    if(fori >= BATCH_REPORT_MAX_COUNT){
                        log(IReportServiceImpl.class,"fori >= BATCH_REPORT_MAX_COUNT");
                        break;
                    }

                    Map.Entry<String,Object> mapEntry = iter.next();
                    String key = mapEntry.getKey();
                    Object reportItem = mapEntry.getValue();

                    removeDataMap.put(key,reportItem);

                    if(reportItem != null){
                        String reportJson = reportItem.toString();

                        try {
                            JSONObject jsonObject = new JSONObject(reportJson);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            log(IReportServiceImpl.class,"startBatchReportLocal jsonArray size = " + jsonArray.length());
                        }

                        ++fori;

                    }

                }

                log(IReportServiceImpl.class,"startBatchReportLocal report size = " + jsonArray.length());

                if(jsonArray.length() > 0){

                    if(NetworkHelper.isNetworkAvailable(AdClientContext.getClientContext())){

                        Logger.printJson(jsonArray.toString(),"startReportV3("+requestUrl+") json data size("+jsonArray.length()+") ↓");

                        JsonArrayPostRequest jsonObjectPostRequest = new JsonArrayPostRequest(requestUrl, jsonArray, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                log(IReportServiceImpl.class,"startBatchReportLocal success , size = " + jsonArray.length());
                                isBatchReport = false;

                                reportDataProvider.delete(removeDataMap);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                isBatchReport = false;
                                log(IReportServiceImpl.class,"startBatchReportLocal error , size = " + jsonArray.length());
                            }
                        });

                        HttpHelper.send(jsonObjectPostRequest);

                    } else {
                        isBatchReport = false;
                    }

                } else {
                    isBatchReport = false;
                }

            }
        });
        return true;
    }

    private void save2Local(final JSONObject reportJson){
        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {

                String key = buildKey();
                try {
                    String message = reportJson.getString("message");
                    if(TextUtils.isEmpty(message)){
                        message = "resend";
                    } else {
                        message = message + "_resend";
                    }
                    reportJson.put("message",message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                reportDataProvider.insert(key,reportJson.toString());

            }
        });
    }

    private String buildKey() {
        String uuid = UUID.randomUUID().toString();
        String time = String.valueOf(System.currentTimeMillis());
        String key = uuid + "_" + time;
        return key;
    }

    private void deleteLocal(final String reportId){
        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {
                reportDataProvider.delete(reportId);
            }
        });
    }

    @Override
    public int getErrorCountToday(String action) {
        return 0;
    }
}
