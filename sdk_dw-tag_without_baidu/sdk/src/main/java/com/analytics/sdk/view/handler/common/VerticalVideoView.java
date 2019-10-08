package com.analytics.sdk.view.handler.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.analytics.sdk.client.AdClientContext;

public class VerticalVideoView extends FrameLayout {
    private VerticalVideoView videoViewLayout;
    private Activity activity;

    private FrameLayout frameVideoLayout;
    private FullScreenVideoView videoView;
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


    private int displayWidth = AdClientContext.displayWidth;
    private int displayHeight = AdClientContext.displayHeight;

    public VerticalVideoView(Activity activity) {
        super(activity);
        videoViewLayout=this;
        this.activity=activity;
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }

    private void initView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoViewLayout.setLayoutParams(layoutParams);
        createLastView();
        createVideo();
        activity.addContentView(videoViewLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void createVideo() {
        frameVideoLayout=new FrameLayout(activity);
        FrameLayout.LayoutParams videoLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameVideoLayout.setLayoutParams(videoLayoutParams);
        addView(frameVideoLayout);
        videoView = new FullScreenVideoView(activity);
        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        videoView.setLayoutParams(videoParams);
        frameVideoLayout.addView(videoView);
        tvSecond=new TextView(activity);
        FrameLayout.LayoutParams secondParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.topMargin = 70*AdClientContext.displayHeight/1920;
        secondParams.leftMargin = 30*AdClientContext.displayWidth/1080;
        tvSecond.setLayoutParams(secondParams);
        tvSecond.setBackgroundColor(Color.parseColor("#8C8C8C8C"));
        tvSecond.setPadding(10,5,10,5);
        tvSecond.setTextSize(14);
        tvSecond.setTextColor(Color.parseColor("#FFFFFF"));
        tvSecond.setSingleLine(true);
        frameVideoLayout.addView(tvSecond);
        detailLayout=new LinearLayout(activity);
        FrameLayout.LayoutParams detailLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        detailLayoutParams.gravity= Gravity.BOTTOM;
        detailLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        detailLayout.setPadding(30*displayWidth/1080,30*displayHeight/1920,30*displayWidth/1080,30*displayHeight/1920);
        detailLayout.setOrientation(LinearLayout.HORIZONTAL);
        detailLayout.setLayoutParams(detailLayoutParams);
        frameVideoLayout.addView(detailLayout);
        createBottomView(detailLayout);

    }

    private void createBottomView(LinearLayout linearLayout) {

        //广告图片的显示
        ivAds = new ImageView(activity);
        ivAds.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageParams3 = new LinearLayout.LayoutParams(170*displayWidth/1080, 170*displayHeight/1920);
        ivAds.setLayoutParams(imageParams3);
        linearLayout.addView(ivAds);


        LinearLayout textLinearLayout=new LinearLayout(activity);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLinearLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        textLinearLayoutParams.weight=1;
        textLinearLayoutParams.leftMargin=20*displayWidth/1080;
        linearLayout.addView(textLinearLayout,textLinearLayoutParams);

        //广告标题的显示
        tvTitle = new TextView(activity);
        LinearLayout.LayoutParams adTitleParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        adTitleParams.weight=1;
        tvTitle.setLayoutParams(adTitleParams);
        tvTitle.setTextSize(17);
        tvTitle.setTextColor(Color.parseColor("#222222"));
        tvTitle.setSingleLine(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        textLinearLayout.addView(tvTitle);

        //广告来源的显示
        tvSource = new TextView(activity);
        LinearLayout.LayoutParams adSourceParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvSource.setLayoutParams(adSourceParams);
        tvSource.setTextSize(14);
        tvSource.setTextColor(Color.parseColor("#666666"));
        tvSource.setSingleLine();
        tvSource.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        textLinearLayout.addView(tvSource);


        clickButton=new Button(activity);
        LinearLayout.LayoutParams clickButtonLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clickButtonLayoutParams.gravity=Gravity.CENTER_VERTICAL;
        clickButton.setLayoutParams(clickButtonLayoutParams);
        clickButton.setText("查看详情");
        clickButton.setTextSize(15);
        clickButton.setTextColor(Color.parseColor("#222222"));
        linearLayout.addView(clickButton);


    }

    private void createLastView() {
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
        addView(tvClose);


        lastLayout=new LinearLayout(activity);
        FrameLayout.LayoutParams lastParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lastParams.gravity=Gravity.CENTER;
        lastParams.leftMargin = 30*AdClientContext.displayWidth/1080;
        lastParams.rightMargin = 30*AdClientContext.displayWidth/1080;
        lastLayout.setOrientation(LinearLayout.VERTICAL);
        lastLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        lastLayout.setAlpha(0.8f);
        lastLayout.setLayoutParams(lastParams);
        addView(lastLayout);

        ivLastAds = new ImageView(activity);
        ivLastAds.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageParams3 = new LinearLayout.LayoutParams(320*displayWidth/1080, 320*displayHeight/1920);
        imageParams3.gravity=Gravity.CENTER_HORIZONTAL;
        ivLastAds.setLayoutParams(imageParams3);
        lastLayout.addView(ivLastAds);


        tvLastTitle = new TextView(activity);
        LinearLayout.LayoutParams adTitleParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adTitleParams.gravity=Gravity.CENTER_HORIZONTAL;
        adTitleParams.topMargin=10*displayHeight/1920;
        tvLastTitle.setLayoutParams(adTitleParams);
        tvLastTitle.setTextSize(15);
        tvLastTitle.setTextColor(Color.parseColor("#222222"));
        tvLastTitle.setSingleLine(true);
        tvLastTitle.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lastLayout.addView(tvLastTitle);


        tvRating = new TextView(activity);
        LinearLayout.LayoutParams tvRatingParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvRatingParams.gravity=Gravity.CENTER_HORIZONTAL;
        tvRatingParams.topMargin=10*displayHeight/1920;
        tvRating.setLayoutParams(tvRatingParams);
        tvRating.setTextSize(15);
        tvRating.setTextColor(Color.parseColor("#222222"));
        tvRating.setSingleLine(true);
        tvRating.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lastLayout.addView(tvRating);

        tvComments = new TextView(activity);
        LinearLayout.LayoutParams tvCommentsParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCommentsParams.gravity=Gravity.CENTER_HORIZONTAL;
        tvCommentsParams.topMargin=10*displayHeight/1920;
        tvComments.setLayoutParams(tvCommentsParams);
        tvComments.setTextSize(15);
        tvComments.setTextColor(Color.parseColor("#222222"));
        tvComments.setSingleLine(true);
        tvComments.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lastLayout.addView(tvComments);

        lastClickButton=new Button(activity);
        LinearLayout.LayoutParams clickButtonLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clickButtonLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;
        clickButtonLayoutParams.topMargin=10*displayHeight/1920;
        lastClickButton.setLayoutParams(clickButtonLayoutParams);
        lastClickButton.setText("查看详情");
        lastClickButton.setTextSize(15);
        lastClickButton.setTextColor(Color.parseColor("#222222"));
        lastLayout.addView(lastClickButton);
    }

    public FrameLayout getFrameVideoLayout() {
        return frameVideoLayout;
    }

    public FullScreenVideoView getVideoView(){
        return videoView;
    }

    public VerticalVideoView getVideoViewLayout() {
        return videoViewLayout;
    }

    public TextView getTvSecond() {
        return tvSecond;
    }

    public LinearLayout getDetailLayout() {
        return detailLayout;
    }

    public ImageView getIvAds() {
        return ivAds;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvSource() {
        return tvSource;
    }

    public Button getClickButton() {
        return clickButton;
    }

    public TextView getTvClose() {
        return tvClose;
    }

    public LinearLayout getLastLayout() {
        return lastLayout;
    }

    public ImageView getIvLastAds() {
        return ivLastAds;
    }

    public TextView getTvLastTitle() {
        return tvLastTitle;
    }

    public TextView getTvRating() {
        return tvRating;
    }

    public TextView getTvComments() {
        return tvComments;
    }

    public Button getLastClickButton() {
        return lastClickButton;
    }
}
