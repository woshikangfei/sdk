package com.analytics.sdk.view.handler.csj.video;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.analytics.sdk.client.video.RewardVideoAdListener;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.activity.WebviewActivity;
import com.analytics.sdk.view.handler.common.FullScreenVideoView;
import com.analytics.sdk.view.handler.common.HorizontalVideoView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RewardVideoHorizontalManger {

    static final String TAG = RewardVideoHorizontalManger.class.getSimpleName();

    private Activity activity;
    private ResponseData responseModel;
    private YdtAdBean ydtAdBean;
    private YdtAdBean.MetaGroupBean metaGroupBean;
    private RewardVideoAdListener adListener;
    private Boolean isCancle=false;
    private int reportTime=0;
    private String className;
    private boolean isPlayComplation;
    private List<YdtAdBean.TracksBean> tracksBeanList;
    //计时器
    private CountDownTimer myCountDownTimer;
    private HorizontalVideoView horizontalVideoView;
    private FrameLayout frameVideoLayout;
    private FullScreenVideoView fullScreenVideoView;
    private TextView tvSecond;
    private LinearLayout detailLayout;
    private ImageView ivAds;
    private TextView tvTitle;
    private TextView tvSource;
    private Button clickButton;
    private TextView tvClose;
    private LinearLayout lastLayout;
    private ImageView ivLastAds;
    private TextView tvLastTitle;
    private TextView tvRating;
    private TextView tvComments;
    private Button lastClickButton;
    private Bitmap adBitmap;
    private Bitmap iconBitmap;


    public RewardVideoHorizontalManger(Activity activity , boolean isPlayComplation, ResponseData responseData, RewardVideoAdListener adListener){
        this.responseModel=responseData;
        this.activity=activity;
        this.adListener=adListener;
        this.isPlayComplation=isPlayComplation;
        className=activity.getClass().getName();
        setRegisterActivityLifecycleCallbacks();
        initView();
        initData();
    }

    private void setRegisterActivityLifecycleCallbacks(){
        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (className.equals(activity.getClass().getName())){
                    Logger.i(TAG,"onActivityResumed");
                    if (isPlayComplation&&fullScreenVideoView!=null){
                        getVideo();
                    }else{
                        close();
                    }

                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (className.equals(activity.getClass().getName())){
                    Logger.i(TAG,"onActivityPaused");
                    stopCountDownTimer();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (className.equals(activity.getClass().getName())){
                    Logger.i(TAG,"onActivityDestroyed");
                }
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        });
    }

    private void stopCountDownTimer() {
        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }

    }

    private void initView() {
        horizontalVideoView=new HorizontalVideoView(activity);
        frameVideoLayout=horizontalVideoView.getFrameVideoLayout();
        frameVideoLayout.setVisibility(View.GONE);
        fullScreenVideoView=horizontalVideoView.getVideoView();
        tvSecond=horizontalVideoView.getTvSecond();
        detailLayout=horizontalVideoView.getDetailLayout();
        ivAds=horizontalVideoView.getIvAds();
        tvTitle=horizontalVideoView.getTvTitle();
        tvSource=horizontalVideoView.getTvSource();
        clickButton=horizontalVideoView.getClickButton();
        tvClose=horizontalVideoView.getTvClose();
        tvClose.setVisibility(View.GONE);
        lastLayout=horizontalVideoView.getLastLayout();
        lastLayout.setVisibility(View.GONE);
        ivLastAds=horizontalVideoView.getIvLastAds();
        tvLastTitle=horizontalVideoView.getTvLastTitle();
        tvRating=horizontalVideoView.getTvRating();
        tvComments=horizontalVideoView.getTvComments();
        lastClickButton=horizontalVideoView.getLastClickButton();

        detailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdClick();
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdClick();
            }
        });

        lastClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdClick();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();

            }
        });
    }

    private void onAdClick() {
        if (adListener!=null){
            adListener.onAdClicked();
        }
        BeanUtils.track(metaGroupBean.getWinCNoticeUrls(),activity,new ClickLoction());
        reportUrl(0);
        if (metaGroupBean.getInteractionType()==1){
            Intent intent=new Intent(activity, WebviewActivity.class);
            intent.putExtra("mClickUrl",metaGroupBean.getClickUrl());
            String title=metaGroupBean.getAdTitle()==null?"":metaGroupBean.getAdTitle();
            intent.putExtra("title",title);
            activity.startActivity(intent);
        }else{
            reportUrl(102000);
            ArrayList<String> arrayList=getReportUrl(102001);
            BeanUtils.startService(activity,ydtAdBean,metaGroupBean.getClickUrl(),"apk",arrayList);
//            adListener.onStartDownload();
            // FIXME: 2019/6/16 需要开始下载的回调吗？
        }
    }

    private void close() {
        Logger.i(TAG,"close()");
        reportUrl(2);
        if (adBitmap!=null&&!adBitmap.isRecycled()){
            adBitmap.recycle();
            adBitmap=null;
        }

        if (iconBitmap!=null&&!iconBitmap.isRecycled()){
            iconBitmap.recycle();
            iconBitmap=null;
        }
        if (myCountDownTimer!=null){
            myCountDownTimer.cancel();
            myCountDownTimer=null;
        }
        if (horizontalVideoView!=null){
            horizontalVideoView.setVisibility(View.GONE);
            horizontalVideoView=null;
        }
        fullScreenVideoView=null;
        if (adListener!=null){
            adListener.onAdDismissed();
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
        getIconImage(ydtAdBean.getAdlogo());
        tvTitle.setText(metaGroupBean.getAdTitle());
        tvLastTitle.setText(metaGroupBean.getAdTitle());
        tvSource.setText(metaGroupBean.getDescs().get(0));
        tvRating.setText("评分："+metaGroupBean.getRating());
        tvComments.setText(metaGroupBean.getComments()+"个评论");
        if (metaGroupBean.getInteractionType()==1){
            clickButton.setText("查看详情");
            lastClickButton.setText("查看详情");
        }else{
            clickButton.setText("立即下载");
            lastClickButton.setText("立即下载");
        }
        getVideo();
    }

    private void getIconImage(String iconUrl) {
        if (PublicUtils.isEmpty(iconUrl)){
            return;
        }

        HttpUtils.getImage(iconUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                iconBitmap = BitmapFactory.decodeStream(stream);
                // 重新设置图片大小
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivAds.setImageBitmap(iconBitmap);
                        ivLastAds.setImageBitmap(iconBitmap);
                    }
                });

            }
        });

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
                        horizontalVideoView.setBackground(new BitmapDrawable(adBitmap));
                    }
                });
            }
        });

    }


    private void getVideo() {
        reportUrl(1);
        if (adListener!=null){
//            adListener.onSucess();
        }
        BeanUtils.track(metaGroupBean.getWinNoticeUrls(),activity,new ClickLoction());
        isCancle=false;
        fullScreenVideoView.setVideoURI(Uri.parse(metaGroupBean.getVideoUrl()));
        fullScreenVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                            fullScreenVideoView.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        fullScreenVideoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
        frameVideoLayout.setVisibility(View.VISIBLE);
        myCountDownTimer=new MyCountDownTimer(metaGroupBean.getVideoDuration()*1000+300, 1000).start();
        fullScreenVideoView.start();
        myCountDownTimer.start();
        reportUrl(101000);
        if (adListener!=null){
            adListener.onAdShow();
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
        tvClose.setVisibility(View.VISIBLE);
        lastLayout.setVisibility(View.VISIBLE);
        if (adListener!=null){
            adListener.onAdVideoCompleted();
        }
        Logger.i(TAG,"onCancle");
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
                        Logger.i(TAG,"视频上报:"+url);
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

        if (iconBitmap!=null&&!iconBitmap.isRecycled()){
            iconBitmap.recycle();
            iconBitmap=null;
        }
    }
}
