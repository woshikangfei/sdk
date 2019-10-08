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


public class HorizontalVideoView extends FrameLayout {
    private HorizontalVideoView videoViewLayout;
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

    public HorizontalVideoView(Activity activity) {
        super(activity);
        videoViewLayout=this;
        this.activity=activity;
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView();
    }

    private void initView() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        videoViewLayout.setLayoutParams(layoutParams);
        createLastView();
        createVideo();
        activity.addContentView(videoViewLayout,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }


    private void createVideo() {
        frameVideoLayout=new FrameLayout(activity);
        LayoutParams videoLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        frameVideoLayout.setLayoutParams(videoLayoutParams);
        addView(frameVideoLayout);
        videoView = new FullScreenVideoView(activity);
        LayoutParams videoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        videoView.setLayoutParams(videoParams);
        frameVideoLayout.addView(videoView);
        tvSecond=new TextView(activity);
        LayoutParams secondParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        secondParams.topMargin = 70*displayHeight/1080;
        secondParams.leftMargin = 30*displayWidth/1920;
        tvSecond.setLayoutParams(secondParams);
        tvSecond.setBackgroundColor(Color.parseColor("#8C8C8C8C"));
        tvSecond.setPadding(10,5,10,5);
        tvSecond.setTextSize(14);
        tvSecond.setTextColor(Color.parseColor("#FFFFFF"));
        tvSecond.setSingleLine(true);
        frameVideoLayout.addView(tvSecond);
        detailLayout=new LinearLayout(activity);
        LayoutParams detailLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        detailLayoutParams.gravity= Gravity.BOTTOM;
        detailLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        detailLayout.setPadding(30*displayWidth/1920,30*displayHeight/1080,30*displayWidth/1920,30*displayHeight/1080);
        detailLayout.setOrientation(LinearLayout.HORIZONTAL);
        detailLayout.setLayoutParams(detailLayoutParams);
        frameVideoLayout.addView(detailLayout);
        createBottomView(detailLayout);

    }

    private void createBottomView(LinearLayout linearLayout) {

        //广告图片的显示
        ivAds = new ImageView(activity);
        ivAds.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageParams3 = new LinearLayout.LayoutParams(170*displayWidth/1920,  170*displayHeight/1080);
        ivAds.setLayoutParams(imageParams3);
        linearLayout.addView(ivAds);


        LinearLayout textLinearLayout=new LinearLayout(activity);
        textLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLinearLayoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        textLinearLayoutParams.weight=1;
        textLinearLayoutParams.leftMargin=20*displayWidth/1920;
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
        lastLayout=new LinearLayout(activity);
        LayoutParams lastParams = new LayoutParams(displayWidth/2,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lastParams.gravity=Gravity.RIGHT;
        lastLayout.setOrientation(LinearLayout.VERTICAL);
        lastLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        lastLayout.setAlpha(0.8f);
        lastLayout.setLayoutParams(lastParams);
        addView(lastLayout);


        tvClose=new TextView(activity);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.gravity=Gravity.RIGHT;
        closeParams.topMargin = 30*displayHeight/1080;
        closeParams.rightMargin = 50*displayWidth/1920;
        tvClose.setLayoutParams(closeParams);
        tvClose.setBackgroundColor(Color.parseColor("#ccadadad"));
        tvClose.setPadding(20,5,20,5);
        tvClose.setTextSize(18);
        tvClose.setText("×");
        tvClose.setTextColor(Color.parseColor("#FFFFFF"));
        lastLayout.addView(tvClose);


        ivLastAds = new ImageView(activity);
        ivLastAds.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams imageParams3 = new LinearLayout.LayoutParams(320*displayWidth/1920, 320*displayHeight/1080);
        imageParams3.gravity=Gravity.CENTER_HORIZONTAL;
        imageParams3.topMargin=80*displayHeight/1080;
        ivLastAds.setLayoutParams(imageParams3);
        lastLayout.addView(ivLastAds);


        tvLastTitle = new TextView(activity);
        LinearLayout.LayoutParams adTitleParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adTitleParams.gravity=Gravity.CENTER_HORIZONTAL;
        adTitleParams.topMargin=10*displayHeight/1080;
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
        tvRatingParams.topMargin=10*displayHeight/1080;
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
        tvCommentsParams.topMargin=10*displayHeight/1080;
        tvComments.setLayoutParams(tvCommentsParams);
        tvComments.setTextSize(15);
        tvComments.setTextColor(Color.parseColor("#222222"));
        tvComments.setSingleLine(true);
        tvComments.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lastLayout.addView(tvComments);

        lastClickButton=new Button(activity);
        LinearLayout.LayoutParams clickButtonLayoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clickButtonLayoutParams.gravity=Gravity.CENTER_HORIZONTAL;
        clickButtonLayoutParams.topMargin=10*displayHeight/1080;
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

    public HorizontalVideoView getVideoViewLayout() {
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
