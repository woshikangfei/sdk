package com.analytics.sdk.view.handler;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ad.entity.ClickBean;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kf on 2018/9/17.
 */

public abstract class BaseHandlerImpl extends RelativeLayout implements View.OnTouchListener {

    public float dianjilv=0.3f;
    protected ClickLoction location;
    protected ResponseData adModel;
    protected ClickBean clickBean;
    //广点通新增字段
    protected String view_id;
    //下载APK的名字
    protected String apkName="apk";

    protected Activity activity;

    protected ConfigBeans bean;

    protected String channelId="default";

    protected int displayWidth = AdClientContext.displayWidth;

    protected int displayHeight = AdClientContext.displayHeight;

    //上报地址
    protected List<String> arrDownloadTrackUrl=new ArrayList<>();
    protected List<String> arrDownloadedTrakUrl=new ArrayList<>();
    protected List<String> arrIntallTrackUrl=new ArrayList<>();
    protected List<String> arrIntalledTrackUrl=new ArrayList<>();
    protected List<String> arrSkipTrackUrl=new ArrayList<>();

    protected boolean isDowload=false;
    // 点击跳转URL
    protected String mClickUrl="";


    protected int apiOrsdkType=0;

    /**广点通的**/
    protected NativeAD mAdManager;
    protected NativeADDataRef nativeUnifiedADBean;

    /**
     * 穿山甲SDK
     * */
    protected TTAdNative mTTAdNative;
    protected TTFeedAd ad;
    protected TTNativeAd ttNativeAd;

    protected int source=0;


    public BaseHandlerImpl(Context context) {
        super(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (location==null){
                    location=new ClickLoction();
                }
                location.setDownTime(System.currentTimeMillis());
                location.setDOWN_X((int)event.getX());
                location.setDOWN_Y((int)event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                location.setUpTime(System.currentTimeMillis());
                location.setUP_X((int)event.getX());
                location.setUP_Y((int)event.getY());
                setOnclick(v);
                break;
            default:
                break;
        }
        return true;
    }


    public abstract void notifyClickListener();

    /**
     * 广点通广告，获取点击信息的请求数据
     * */
    protected String toClickJson(){
        String result="";
        try {
            JSONObject gdtClickJson=new JSONObject();
            gdtClickJson.put("view_id",adModel.getAds().get(0).getView_id());
            gdtClickJson.put("key",adModel.getAds().get(0).getMetaGroup().get(0).getClick_key());
            gdtClickJson.put("interact_type", adModel.getAds().get(0).getMetaGroup().get(0).getInteractionType());
            gdtClickJson.put("req_width",location.getAdWidth());
            gdtClickJson.put("req_height",location.getAdHeight());
            gdtClickJson.put("width",location.getFinalwidth());
            gdtClickJson.put("height",location.getFinalHeight());
            gdtClickJson.put("down_x",location.getDOWN_X());
            gdtClickJson.put("down_y",location.getDOWN_Y());
            gdtClickJson.put("up_x",location.getUP_X());
            gdtClickJson.put("up_y",location.getUP_Y());
            result=java.net.URLEncoder.encode(gdtClickJson.toString(),"utf-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




    /**
     * 广点通广告，请求下载类型点击信息
     * */
    protected void startGet(final String path){
        HttpUtils.sendHttpRequestForGet(path, new HttpUtils.RequestListener() {
            @Override
            public void onFinish(String response) {
                try {
                    if (!PublicUtils.isEmpty(response)) {
                        // 解析返回数据
                        JSONObject jsonObject=new JSONObject(response);
                        clickBean=new ClickBean();
                        if (jsonObject.has("ret")){
                            clickBean.setRet(jsonObject.getInt("ret"));
                        }
                        if (jsonObject.has("data")){
                            ClickBean.DataBean dataBean=new ClickBean.DataBean();
                            JSONObject dataObject=jsonObject.getJSONObject("data");
                            if (dataObject.has("clickid")){
                                dataBean.setClickid(dataObject.getString("clickid"));
                            }
                            if (dataObject.has("dstlink")){
                                dataBean.setDstlink(dataObject.getString("dstlink"));
                            }
                            clickBean.setData(dataBean);
                        }
                        mClickUrl=clickBean.getData().getDstlink();
                        doDownload();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
            }

        },activity);
    }

    protected void doDownload(){
        if (isDowload){
            return;
        }
        if(!PermissionsHelper.isGrantExternalRW(activity)) {
            return;
        }

        location.setACTION_ID(5);
        if(clickBean!=null&&clickBean.getData()!=null){
            location.setCLICK_ID(clickBean.getData().getClickid());
        }
        BeanUtils.track(adModel.getAds().get(0).getMetaGroup().get(0).getArrDownloadTrackUrl(),activity,location);
        isDowload=true;
        //非广点通广告
        BeanUtils.startService(activity,adModel,mClickUrl,apkName,location);
    }


    /**
     * 广告点击事件的处理逻辑
     * */
    protected void onclick(YdtAdBean.MetaGroupBean metaGroupBean, YdtAdBean ydtAdBean, int source){
        if (NetworkHelper.isNetworkAvailable(activity)&&metaGroupBean!=null) {

            notifyClickListener();

            //点击上报
            BeanUtils.track(metaGroupBean.getWinCNoticeUrls(),activity,location);

            int interactionType = metaGroupBean.getInteractionType();
            //广点通,
            if (source==DataSource.API_GDT){
                if (interactionType==1){
                    if (!PublicUtils.isEmpty(mClickUrl)){
                        jumpToWebview();
                    }
                }else if(interactionType==2){
                    if (ydtAdBean.getProtocolType()==1){
                        startGet(BeanUtils.buildUrl(mClickUrl,location));
                    }else{
                        // stopCountDownTimer();
                        Toast.makeText(activity,"开始下载",1000*3).show();
                        doDownload();
                    }
                }
                return;
            }

            String deepLink = metaGroupBean.getDeepLink();
            String strLinkUrl =metaGroupBean.getStrLinkUrl();
            String downloadLink =metaGroupBean.getDownloadLink();

            if (interactionType ==2){
                if (!PublicUtils.isEmpty(deepLink)&& PublicUtils.hasApplication(activity,deepLink)){
                    mClickUrl=deepLink;
                    jumpToWebview();
                }else {
                    if (!PublicUtils.isEmpty(downloadLink)) {
                        mClickUrl = downloadLink;
                    }
                    if (!PublicUtils.isEmpty(mClickUrl)){
                        // stopCountDownTimer();
                        Toast.makeText(activity,"开始下载",1000*3).show();
                        doDownload();
                    }
                }
            }else{
                if (!PublicUtils.isEmpty(deepLink)&& PublicUtils.hasApplication(activity,deepLink)){
                    mClickUrl=deepLink;
                }else if(!PublicUtils.isEmpty(strLinkUrl)){
                    mClickUrl=strLinkUrl;
                }
                if (!PublicUtils.isEmpty(mClickUrl)){
                    jumpToWebview();
                }
            }
        }
    }


    protected abstract void setOnclick(View v);
    protected abstract void jumpToWebview();

    protected abstract void getAdLogo(String logo);
    protected abstract void getBitmap(String logo);
}
