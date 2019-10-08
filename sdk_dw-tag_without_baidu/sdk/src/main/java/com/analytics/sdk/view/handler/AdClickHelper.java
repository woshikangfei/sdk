package com.analytics.sdk.view.handler;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ClickBean;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.activity.WebviewActivity;

import org.json.JSONObject;

import java.util.List;

// FIXME: 2019/6/16 后面要优化
public class AdClickHelper {

    /**
     * 广告点击事件的处理逻辑
     * */
    public static boolean handleClick(AdResponse adResponse, YdtAdBean ydtAdBean, ClickLoction clickLoction){

        Activity activity = adResponse.getClientRequest().getActivity();

        YdtAdBean.MetaGroupBean metaGroupBean = ydtAdBean.getMetaGroup().get(0);

        int source = adResponse.getResponseData().getSource();
        String clickUrl = metaGroupBean.getClickUrl();
        String apkName = "";

        if (NetworkHelper.isNetworkAvailable(activity) && metaGroupBean != null) {

            //点击上报
            BeanUtils.track(metaGroupBean.getWinCNoticeUrls(),activity,clickLoction);

            int interactionType = metaGroupBean.getInteractionType();
            //广点通,
            if (source == DataSource.API_GDT){
                if (interactionType==1){
                    if (!PublicUtils.isEmpty(clickUrl)){
                        jumpToWebview(activity,clickUrl,metaGroupBean);
                    }
                }else if(interactionType==2){
                    if (ydtAdBean.getProtocolType()==1){
                        startGet(activity,BeanUtils.buildUrl(clickUrl,clickLoction),adResponse.getResponseData(),clickLoction,apkName);
                    }else{
                        // stopCountDownTimer();
                        Toast.makeText(activity,"开始下载",Toast.LENGTH_LONG).show();
                        doDownload(activity,adResponse.getResponseData(),clickLoction,null,clickUrl,apkName);
                    }
                }
                return true;
            }

            String deepLink = metaGroupBean.getDeepLink();
            String strLinkUrl =metaGroupBean.getStrLinkUrl();
            String downloadLink =metaGroupBean.getDownloadLink();

            if (interactionType ==2){
                if (!PublicUtils.isEmpty(deepLink)&& PublicUtils.hasApplication(activity,deepLink)){
                    clickUrl=deepLink;
                    jumpToWebview(activity,clickUrl,metaGroupBean);
                }else {
                    if (!PublicUtils.isEmpty(downloadLink)) {
                        clickUrl = downloadLink;
                    }
                    if (!PublicUtils.isEmpty(clickUrl)){
                        // stopCountDownTimer();
                        Toast.makeText(activity,"开始下载",Toast.LENGTH_LONG).show();
                        doDownload(activity,adResponse.getResponseData(),clickLoction,null,clickUrl,apkName);
                    }
                }
            }else{
                if (!PublicUtils.isEmpty(deepLink)&& PublicUtils.hasApplication(activity,deepLink)){
                    clickUrl = deepLink;
                }else if(!PublicUtils.isEmpty(strLinkUrl)){
                    clickUrl = strLinkUrl;
                }
                if (!PublicUtils.isEmpty(clickUrl)){
                    jumpToWebview(activity,clickUrl,metaGroupBean);
                }
            }
            return true;
        }
        return false;
    }

    static boolean isDowload = false;

    static void doDownload(Activity activity, ResponseData responseData,ClickLoction clickLoction, String clickId,String clickUrl,String apkName){
        if (isDowload){
            return;
        }
        if(!PermissionsHelper.isGrantExternalRW(activity)) {
            return;
        }

        clickLoction.setACTION_ID(5);
        clickLoction.setCLICK_ID(clickId);

        BeanUtils.track(responseData.getAds().get(0).getMetaGroup().get(0).getArrDownloadTrackUrl(),activity,clickLoction);
        isDowload = true;
        //非广点通广告
        BeanUtils.startService(activity,responseData,clickUrl,apkName,clickLoction);
//        listener.onStartDownload();
    }

    public static void onDownloadCompleted(){
        isDowload = false;
    }


    /**
     * 广点通广告，请求下载类型点击信息
     * */
    static void startGet(final Activity activity, final String path, final ResponseData responseData, final ClickLoction clickLoction, final String apkName){
        HttpUtils.sendHttpRequestForGet(path, new HttpUtils.RequestListener() {
            @Override
            public void onFinish(String response) {
                try {
                    if (!PublicUtils.isEmpty(response)) {
                        // 解析返回数据
                        JSONObject jsonObject=new JSONObject(response);
                        ClickBean clickBean = new ClickBean();
                        if (jsonObject.has("ret")){
                            clickBean.setRet(jsonObject.getInt("ret"));
                        }
                        String clickId = null;
                        String clickUrl = null;
                        if (jsonObject.has("data")){
                            ClickBean.DataBean dataBean=new ClickBean.DataBean();
                            JSONObject dataObject=jsonObject.getJSONObject("data");
                            if (dataObject.has("clickid")){
                                clickId = dataObject.getString("clickid");
                                dataBean.setClickid(clickId);
                            }
                            if (dataObject.has("dstlink")){
                                clickUrl = dataObject.getString("dstlink");
                                dataBean.setDstlink(clickUrl);
                            }
                            clickBean.setData(dataBean);
                        }
                        doDownload(activity,responseData,clickLoction,clickId,clickUrl,apkName);
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


    /**
     * 跳转到落地页
     * */
    public static void jumpToWebview(Activity activity, String clickUrl, YdtAdBean.MetaGroupBean metaGroupBean){
        Intent intent=new Intent(activity, WebviewActivity.class);
        intent.putExtra("mClickUrl",clickUrl);
        String title=metaGroupBean.getAdTitle()==null?"":metaGroupBean.getAdTitle();
        intent.putExtra("title",title);
        List<String> arrDownloadTrackUrl = metaGroupBean.getArrDownloadTrackUrl();
        if(arrDownloadTrackUrl != null && arrDownloadTrackUrl.size() > 0){
            intent.putExtra("arrDownloadTrackUrl",arrDownloadTrackUrl.toArray(new String[]{}));
        }
        activity.startActivity(intent);
    }

}
