package com.analytics.sdk.view.handler.csj.video;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.handler.common.FullScreenVideoView;
import com.analytics.sdk.view.handler.common.JiGuangVerticalVideoView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JiGuangRewardVideoVertailManger {
    private Activity activity;
    private ResponseData responseModel;
    private YdtAdBean ydtAdBean;
    private YdtAdBean.MetaGroupBean metaGroupBean;
    private RewardVideoAdListener clientRewardVideoAdListener;
    private Boolean isCancle=false;
    private int reportTime=0;
    private boolean isPlayComplation;
    private List<YdtAdBean.TracksBean> tracksBeanList;
    //计时器
    private CountDownTimer myCountDownTimer;
    private JiGuangVerticalVideoView jiGuangVerticalVideoView;
    private FrameLayout frameVideoLayout;
    private FullScreenVideoView fullScreenVideoView;
    private TextView tvSecond;
    private FrameLayout lastFrameLayout;
    private TextView tvClose;
    private Bitmap adBitmap;
    private WebView webView;
    private boolean isDownload=false;
    private ImageView ivBackground;

    public JiGuangRewardVideoVertailManger(Activity activity , boolean isPlayComplation, ResponseData responseModel, RewardVideoAdListener adListener){
        this.responseModel=responseModel;
        this.activity=activity;
        this.clientRewardVideoAdListener =adListener;
        this.isPlayComplation=isPlayComplation;
        isDownload=false;
        initView();
        initData();
    }

    private void initView() {
        jiGuangVerticalVideoView=new JiGuangVerticalVideoView(activity);
        frameVideoLayout=jiGuangVerticalVideoView.getFrameVideoLayout();
        ivBackground=jiGuangVerticalVideoView.getIvBackground();
        fullScreenVideoView=jiGuangVerticalVideoView.getVideoView();
        tvSecond=jiGuangVerticalVideoView.getTvSecond();
        webView=jiGuangVerticalVideoView.getWebView();
        lastFrameLayout=jiGuangVerticalVideoView.getLastFrameLayout();
        tvClose=jiGuangVerticalVideoView.getTvClose();
        lastFrameLayout.setVisibility(View.GONE);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP){
                    if (clientRewardVideoAdListener !=null){
                        clientRewardVideoAdListener.onAdClicked();
                    }
                    BeanUtils.track(metaGroupBean.getWinCNoticeUrls(),activity,new ClickLoction());
                    reportUrl(0);
                }
                return false;
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Logger.e("splash","setDownloadListener");
                if (isDownload){
                    return;
                }
                isDownload=true;
                Toast.makeText(activity,"开始下载",Toast.LENGTH_SHORT).show();
                reportUrl(102000);
                BeanUtils.track(ydtAdBean.getMetaGroup().get(0).getArrDownloadTrackUrl(),activity,new ClickLoction());
                ArrayList<String> arrayList=getReportUrl(102001);
                if (metaGroupBean.getArrIntalledTrackUrl()!=null){
                    arrayList.addAll(metaGroupBean.getArrIntalledTrackUrl());
                }
                BeanUtils.startService(activity,ydtAdBean,url,"apk",arrayList);
//                clientRewardVideoAdListener.onStartDownload();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    private void close() {
        reportUrl(2);
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }
        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }
        if (jiGuangVerticalVideoView!=null){
            jiGuangVerticalVideoView.setVisibility(View.GONE);
            jiGuangVerticalVideoView=null;
        }
        fullScreenVideoView=null;
        if (clientRewardVideoAdListener !=null){
            clientRewardVideoAdListener.onAdDismissed();
        }
    }

    private void initData() {
        ydtAdBean=responseModel.getAds().get(0);
        metaGroupBean=ydtAdBean.getMetaGroup().get(0);
        if (ydtAdBean.getTracks()!=null&&ydtAdBean.getTracks().size()>0){
            tracksBeanList=ydtAdBean.getTracks();
        }
        if (metaGroupBean.getImageUrl()!=null&&metaGroupBean.getImageUrl().size()>0){
            getBackgroundImage(metaGroupBean.getImageUrl().get(0));
        }
        getVideo();
    }



    /**
     * 请求背景图片
     * */
    private void getBackgroundImage(String imageUrl) {
        if (PublicUtils.isEmpty(imageUrl)){
            return;
        }

        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                adBitmap = BitmapFactory.decodeStream(stream);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivBackground.setBackground(new BitmapDrawable(adBitmap));
                    }
                });
            }
        });

    }


    private void getVideo() {
        isCancle=false;
        if (clientRewardVideoAdListener !=null){
//            clientRewardVideoAdListener.onSucess();
        }
        fullScreenVideoView.setVideoURI(Uri.parse(metaGroupBean.getVideoUrl()));
//        fullScreenVideoView.setVideoPath("file:////android_asset/test.mp4");

//        fullScreenVideoView.setVideoPath("android.resource://com.mf.LDZJCQ.sougou/raw/test.mp4");
//        fullScreenVideoView.setVideoPath("android.resource://com.mf.LDZJCQ.sougou/raw/"+String.valueOf(Ads.ids));

        fullScreenVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                            fullScreenVideoView.setBackgroundColor(Color.TRANSPARENT);
                            ivBackground.setVisibility(View.GONE);
                            myCountDownTimer=new MyCountDownTimer(metaGroupBean.getVideoDuration()*1000+300, 1000).start();
                            myCountDownTimer.start();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        fullScreenVideoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
        fullScreenVideoView.start();
        reportUrl(101000);
        if (clientRewardVideoAdListener !=null){
            clientRewardVideoAdListener.onAdShow();
        }
    }

    /**
     * 倒计时控件
     * */
    class MyCountDownTimer extends CountDownTimer {
        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            onCancle();
        }
        @Override
        public void onTick(long millisUntilFinished) {
            reportTime=metaGroupBean.getVideoDuration()-(int)millisUntilFinished/1000;
            if (tvSecond != null) {
                tvSecond.setText(""+millisUntilFinished/1000);
            }
        }
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            reportUrl(101002);
            onCancle();
        }
    }

    private void onCancle(){
        if (isCancle){
            return;
        }
        isCancle=true;
        fullScreenVideoView=null;
        frameVideoLayout.setVisibility(View.GONE);
        webView.loadDataWithBaseURL(null,metaGroupBean.getEnd_card_html(), "text/html", "utf-8", null);
        reportUrl(1);
        BeanUtils.track(metaGroupBean.getWinNoticeUrls(),activity,new ClickLoction());
        lastFrameLayout.setVisibility(View.VISIBLE);
        if (clientRewardVideoAdListener !=null){
            clientRewardVideoAdListener.onAdVideoCompleted();
        }
    }

    private void reportUrl(int type){
        if (tracksBeanList!=null){
            for (int i=0;i<tracksBeanList.size();i++){
                if (tracksBeanList.get(i).getType()==type){
                    List<String> urls=tracksBeanList.get(i).getUrls();
                    for (int j=0;j<urls.size();j++){
                        String url=urls.get(j);
                        if (url.contains("{PROGRESS}")){
                            url=url.replace("{PROGRESS}",String.valueOf(reportTime));
                        }
                        HttpUtils.sendHttpRequestForGet(url, null,activity);
                    }

                }
            }
        }
    }

    private ArrayList<String> getReportUrl(int type){
        ArrayList<String> arrayList=new ArrayList<>();
        if (tracksBeanList!=null){
            for (int i=0;i<tracksBeanList.size();i++){
                if (tracksBeanList.get(i).getType()==type){
                    List<String> urls=tracksBeanList.get(i).getUrls();
                    for (int j=0;j<urls.size();j++){
                        String url=urls.get(j);
                        if (url.contains("{PROGRESS}")){
                            url=url.replace("{PROGRESS}",String.valueOf(metaGroupBean.getVideoDuration()));
                            arrayList.add(url);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public void onDestory(){
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }

    }
}
