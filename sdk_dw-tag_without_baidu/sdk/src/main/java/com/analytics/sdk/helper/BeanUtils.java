package com.analytics.sdk.helper;

import android.content.Context;
import android.content.Intent;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.service.download.IDownloadServiceImpl;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kf on 2018/9/20.
 */
// FIXME: 2019/6/16 结构问题，扔到IReportService中
public class BeanUtils {

    static final String TAG = "BeanUtils";

    public static void track(String[] trackUrls, Context context, ClickLoction clickLoction){
        if(trackUrls!=null){
            track(Arrays.asList(trackUrls),context,clickLoction);
        }
    }

    // TODO: 2019/6/13 走上报接口
    public static void track(List<String> trackUrls, Context context, ClickLoction clickLoction){
        Logger.i(TAG,"track enter, trackUrl list size = " + (trackUrls != null ? trackUrls.size() : 0));

        if (context != null && clickLoction != null && trackUrls != null && trackUrls.size() > 0){
            for (int i=0;i<trackUrls.size();i++){
                String clickReportUrl=trackUrls.get(i);
                clickReportUrl=buildUrl(clickReportUrl,clickLoction);

                final String finalClickReport = clickReportUrl;

                Logger.i(TAG,"report url = " + finalClickReport);

                HttpUtils.sendHttpRequestForGet(finalClickReport, new HttpUtils.RequestListener() {
                    @Override
                    public void onFinish(String response) {
                        Logger.i(TAG,"report success = " + finalClickReport);
                    }

                    @Override
                    public void onError(String message) {
                        Logger.i(TAG,"report error = " + finalClickReport);
                    }
                }, context);
            }
        }
    }

    // TODO: 2019/6/13 走上报接口
    public static void track(String trackUrls, Context context, ClickLoction clickLoction){
        Logger.i(TAG,"track enter, track url = " + (trackUrls));
        String clickReportUrl=trackUrls;
        if(clickLoction!=null&&context!=null){
            clickReportUrl=buildUrl(clickReportUrl,clickLoction);
        }
        final String finalClickReport = clickReportUrl;
        Logger.i(TAG,"report url = " + finalClickReport);
        HttpUtils.sendHttpRequestForGet(clickReportUrl, new HttpUtils.RequestListener() {
            @Override
            public void onFinish(String response) {
                Logger.i(TAG,"report success = " + finalClickReport);
            }

            @Override
            public void onError(String message) {
                Logger.i(TAG,"report error = " + finalClickReport);
            }
        }, context);
    }

    public static String buildUrl(String url, ClickLoction clickLoction){
        if(PublicUtils.isEmpty(url)||clickLoction==null){
            return null;
        }
        url = url.replace("__REQ_WIDTH__",String.valueOf(clickLoction.getAdWidth()));
        url=url.replace("__REQ_HEIGHT__",String.valueOf(clickLoction.getAdHeight()));
        url=url.replace("__WIDTH__",String.valueOf(clickLoction.getFinalwidth()));
        url=url.replace("__HEIGHT__",String.valueOf(clickLoction.getFinalHeight()));
        url=url.replace("__DOWN_X__",String.valueOf(clickLoction.getDOWN_X()));
        url=url.replace("__DOWN_Y__",String.valueOf(clickLoction.getDOWN_Y()));
        url=url.replace("__UP_X__",String.valueOf(clickLoction.getUP_X()));
        url=url.replace("__UP_Y__",String.valueOf(clickLoction.getUP_Y()));
        if (url.contains("__ACTION_ID__")){
            url=url.replace("__ACTION_ID__",String.valueOf(clickLoction.getACTION_ID()));
        }
        if (url.contains("__CLICK_ID__")&&null!=clickLoction.getCLICK_ID()){
            url=url.replace("__CLICK_ID__",clickLoction.getCLICK_ID());
        }
        if (url.contains("__EVENT_TIME_START__")){
            url=url.replace("__EVENT_TIME_START__",""+clickLoction.getDownTime());
        }
        if (url.contains("__EVENT_TIME_END__")){
            url=url.replace("__EVENT_TIME_END__",""+clickLoction.getUpTime());
        }
        return url;
    }


    /**
     * 广点通广告，获取展示上报信息的请求数据
     * */
    public static String toShowJson(String view_id, String key){
        String result="";
        try {
            JSONObject gdtShowJson=new JSONObject();
            gdtShowJson.put("view_id",view_id);
            gdtShowJson.put("key",key);
            result=java.net.URLEncoder.encode(gdtShowJson.toString(),"utf-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void startService(Context activity, ResponseData adModel, String clickUrl, String apkName, ClickLoction clickLoction){
        try{
            if (activity==null||adModel==null||PublicUtils.isEmpty(clickUrl)){
                return;
            }
            Intent intent=new Intent(activity, IDownloadServiceImpl.class);
            String adKey ="";
            if (PublicUtils.isEmpty(adModel.getAds().get(0).getAdKey())) {
                adKey=Long.toString(System.currentTimeMillis());
            }else{
                adKey=adModel.getAds().get(0).getAdKey();
            }
            intent.putExtra("adTitle",adModel.getAds().get(0).getMetaGroup().get(0).getAdTitle());
            intent.putExtra("clickUrl",clickUrl);
            intent.putExtra("clickid",adKey);
            intent.putExtra("apkName",apkName);
            intent.putStringArrayListExtra("arrDownloadedTrakUrl",(ArrayList<String>) adModel.getAds().get(0).getMetaGroup().get(0).getArrDownloadedTrakUrl());
            intent.putStringArrayListExtra("arrIntallTrackUrl",(ArrayList<String>)adModel.getAds().get(0).getMetaGroup().get(0).getArrIntallTrackUrl());
            intent.putStringArrayListExtra("arrIntalledTrackUrl",(ArrayList<String>)adModel.getAds().get(0).getMetaGroup().get(0).getArrIntalledTrackUrl());

            if(clickLoction!=null){
                intent.putExtra("clickLoction",clickLoction);
            }

            activity.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void startService(Context activity, ResponseData adModel, String mClickUrl, String apkName){
        startService(activity,adModel,mClickUrl,apkName,new ClickLoction());
    }


    public static void startService(Context activity, YdtAdBean adsBean, String mClickUrl, String apkName){
        try {
            if (adsBean==null||PublicUtils.isEmpty(mClickUrl)||activity==null){
                return;
            }
            Intent intent=new Intent(activity, IDownloadServiceImpl.class);
            String adKey ="";
            if (PublicUtils.isEmpty(adsBean.getAdKey())) {
                adKey=Long.toString(System.currentTimeMillis());
            }else{
                adKey=adsBean.getAdKey();
            }
            intent.putExtra("adTitle",adsBean.getMetaGroup().get(0).getAdTitle());
            intent.putExtra("clickUrl",mClickUrl);
            intent.putExtra("clickid",adKey);
            intent.putExtra("apkName",apkName);
            intent.putStringArrayListExtra("arrDownloadedTrakUrl",(ArrayList<String>) adsBean.getMetaGroup().get(0).getArrDownloadedTrakUrl());
            intent.putStringArrayListExtra("arrIntallTrackUrl",(ArrayList<String>)adsBean.getMetaGroup().get(0).getArrIntallTrackUrl());
            intent.putStringArrayListExtra("arrIntalledTrackUrl",(ArrayList<String>)adsBean.getMetaGroup().get(0).getArrIntalledTrackUrl());
            activity.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void startService(Context activity, YdtAdBean ydtAdBean, String mClickUrl, String apkName, ArrayList<String> url){
        startService(activity,ydtAdBean,mClickUrl,apkName,url,new ClickLoction());
    }

    public static void startService(Context activity, YdtAdBean ydtAdBean, String mClickUrl, String apkName, ArrayList<String> url, ClickLoction clickLoction){
        try{
            if (activity==null||ydtAdBean==null||PublicUtils.isEmpty(mClickUrl)){
                return;
            }
            Intent intent=new Intent(activity, IDownloadServiceImpl.class);
            String adKey ="";
            if (PublicUtils.isEmpty(ydtAdBean.getAdKey())) {
                adKey=Long.toString(System.currentTimeMillis());
            }else{
                adKey=ydtAdBean.getAdKey();
            }
            intent.putExtra("adTitle",ydtAdBean.getMetaGroup().get(0).getAdTitle());
            intent.putExtra("clickUrl",mClickUrl);
            intent.putExtra("clickid",adKey);
            intent.putExtra("apkName",apkName);
            intent.putStringArrayListExtra("arrDownloadedTrakUrl",(ArrayList<String>) ydtAdBean.getMetaGroup().get(0).getArrDownloadedTrakUrl());
            intent.putStringArrayListExtra("arrIntallTrackUrl",(ArrayList<String>) ydtAdBean.getMetaGroup().get(0).getArrIntallTrackUrl());
            intent.putStringArrayListExtra("arrIntalledTrackUrl",url);
            if(clickLoction!=null){
                intent.putExtra("clickLoction",clickLoction);
            }
            Logger.e("splash",(ArrayList<String>) ydtAdBean.getMetaGroup().get(0).getArrDownloadedTrakUrl()+"    "+(ArrayList<String>) ydtAdBean.getMetaGroup().get(0).getArrIntallTrackUrl()+"    ");
            for (int i=0;i<url.size();i++){
                Logger.e("splash",url.get(0));
            }
            activity.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
