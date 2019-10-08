package com.analytics.sdk.view.handler.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.analytics.sdk.client.AdClientContext;

public class JiGuangVerticalVideoView extends FrameLayout {
    private JiGuangVerticalVideoView videoViewLayout;
    private ImageView ivBackground;
    private Activity activity;

    private FrameLayout frameVideoLayout;
    private FullScreenVideoView videoView;
    private TextView tvSecond;

    private FrameLayout lastFrameLayout;
    private JIGuangRewardWebview webView;
    private TextView tvClose;

    public JiGuangVerticalVideoView(Activity activity) {
        super(activity);
        videoViewLayout=this;
        this.activity=activity;
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }

    private void initView() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        videoViewLayout.setLayoutParams(layoutParams);
        createLastView();
        createVideo();
        createImage();
        activity.addContentView(videoViewLayout,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void createImage() {
        ivBackground=new ImageView(activity);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ivBackground.setLayoutParams(layoutParams);
        addView(ivBackground);
    }

    private void createVideo() {
        frameVideoLayout=new FrameLayout(activity);
        LayoutParams videoLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        frameVideoLayout.setLayoutParams(videoLayoutParams);
        frameVideoLayout.setBackgroundColor(Color.parseColor("#000000"));
        addView(frameVideoLayout);
        videoView = new FullScreenVideoView(activity);
        FrameLayout.LayoutParams videoParams = new  FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        videoParams.gravity=Gravity.CENTER;
        videoView.setLayoutParams(videoParams);
        frameVideoLayout.addView(videoView);
        tvSecond=new TextView(activity);
        LayoutParams secondParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.topMargin = 70*AdClientContext.displayHeight /1920;
        secondParams.leftMargin = 30*AdClientContext.displayWidth/1080;
        tvSecond.setLayoutParams(secondParams);
        tvSecond.setBackgroundColor(Color.parseColor("#8C8C8C8C"));
        tvSecond.setPadding(10,5,10,5);
        tvSecond.setTextSize(14);
        tvSecond.setTextColor(Color.parseColor("#FFFFFF"));
        tvSecond.setSingleLine(true);
        frameVideoLayout.addView(tvSecond);
    }


    private void createLastView() {
        lastFrameLayout=new FrameLayout(activity);
        LayoutParams lastParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lastFrameLayout.setLayoutParams(lastParams);
        addView(lastFrameLayout);

        FrameLayout.LayoutParams webviewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView=new JIGuangRewardWebview(activity);
        webView.setLayoutParams(webviewParams);
        lastFrameLayout.addView(webView);

        tvClose=new TextView(activity);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.gravity=Gravity.RIGHT;
        closeParams.topMargin = 70*AdClientContext.displayHeight/1920;
        closeParams.rightMargin = 30*AdClientContext.displayWidth/1080;
        tvClose.setLayoutParams(closeParams);
        tvClose.setBackgroundColor(Color.parseColor("#bb8C8C8C"));
        tvClose.setPadding(20,5,20,5);
        tvClose.setTextSize(18);
        tvClose.setText("×");
        tvClose.setTextColor(Color.parseColor("#FFFFFF"));
        lastFrameLayout.addView(tvClose);
    }

    public FrameLayout getFrameVideoLayout() {
        return frameVideoLayout;
    }

    public FullScreenVideoView getVideoView(){
        return videoView;
    }

    public JiGuangVerticalVideoView getVideoViewLayout() {
        return videoViewLayout;
    }

    public TextView getTvSecond() {
        return tvSecond;
    }

    public FrameLayout getLastFrameLayout(){
        return lastFrameLayout;
    }

    public WebView getWebView(){
        return webView;
    }

    public TextView getTvClose(){
        return tvClose;
    }

    public ImageView getIvBackground(){
        return ivBackground;
    }
}
