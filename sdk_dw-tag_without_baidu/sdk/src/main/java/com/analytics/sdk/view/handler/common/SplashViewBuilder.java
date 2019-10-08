package com.analytics.sdk.view.handler.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.YdtAdBean;
import com.analytics.sdk.view.strategy.StrategyRootLayout;

import java.io.InputStream;
import java.util.List;

public class SplashViewBuilder {

    static final String TAG = SplashViewBuilder.class.getSimpleName();

    private StrategyRootLayout splashRootLayout;
    /**
     * 广告图片
     */
    private ImageView adBitmapView;
    /**
     * 应用的LOGO
     */
    private ImageView appBitmapView;
    /**
     * 广告名称
     */
    private TextView adTitleView;
    /**
     * 广告的logo(图片LOGO)
     */
    private ImageView adLogoView;
    /**
     *
     * 如果图片LOGO没有正常显示则显示文本LOGO
     *
     * 广告的文本logo(例如：广告这两个字)
     */
    private TextView adTextLogoView;
    /**
     * 跳过按钮
     */
    private TextView adSkipView;

    private AdResponse adResponse;
    /**
     * 广告数据
     */
    private YdtAdBean ydtAdBean;
    private Activity activity;

    /**
     * 广告图片
     */
    private Bitmap adBitmap;
    /**
     * APP的logo
     */
    private Bitmap appLogoBitmap;

    private BuildListener builderListener;

    public interface BuildListener {
        void onShow(SplashViewBuilder builder);
        void onError();
        void onSkipClicked();
    }

    public static SplashViewBuilder build(AdResponse adResponse, YdtAdBean ydtAdBean, BuildListener viewClick){
        SplashViewBuilder builder = new SplashViewBuilder();

        builder.activity = adResponse.getClientRequest().getActivity();
        builder.ydtAdBean = ydtAdBean;
        builder.adResponse = adResponse;
        builder.builderListener = viewClick;

        return builder;
    }


    private void initFullView() {

        StrategyRootLayout splashRootLayout = (StrategyRootLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_api_fill_layout,null);

        adBitmapView = splashRootLayout.findViewById(R.id.ad_bitmap);
        appBitmapView = splashRootLayout.findViewById(R.id.app_logo);
        adSkipView = splashRootLayout.findViewById(R.id.ad_skip);
        this.splashRootLayout = splashRootLayout;

    }

    /**
     * 广告图片在标题上面
     */
    public void initViewImageTop(){
        StrategyRootLayout splashRootLayout = (StrategyRootLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_feedlist_fill_layout,null);

        adBitmapView = splashRootLayout.findViewById(R.id.ad_bitmap);
        appBitmapView = splashRootLayout.findViewById(R.id.app_logo);
        adTitleView = splashRootLayout.findViewById(R.id.ad_title);
        adSkipView = splashRootLayout.findViewById(R.id.ad_skip);
        adLogoView = splashRootLayout.findViewById(R.id.ad_logo);
        adTextLogoView = splashRootLayout.findViewById(R.id.ad_textlogo);
        this.splashRootLayout = splashRootLayout;

    }

    /**
     * 广告图片在标题下面
     */
    public void initViewTextTop(){
        StrategyRootLayout splashRootLayout = (StrategyRootLayout) activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_feedlist_fill_layout2,null);

        adBitmapView = splashRootLayout.findViewById(R.id.ad_bitmap);
        appBitmapView = splashRootLayout.findViewById(R.id.app_logo);
        adTitleView = splashRootLayout.findViewById(R.id.ad_title);
        adSkipView = splashRootLayout.findViewById(R.id.ad_skip);
        adLogoView = splashRootLayout.findViewById(R.id.ad_logo);
        adTextLogoView = splashRootLayout.findViewById(R.id.ad_textlogo);
        this.splashRootLayout = splashRootLayout;

    }

    /**
     * 根据服务器配置调整广告图片与广告标题之间的间隔
     */
    public void justViewMargin(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adBitmapView.getLayoutParams();

        layoutParams.leftMargin = lMargin * AdClientContext.displayWidth / 1080;
        layoutParams.rightMargin = rMargin * AdClientContext.displayWidth / 1080;
        layoutParams.topMargin = top * AdClientContext.displayHeight / 1920;
        adBitmapView.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams adTitleParams = (RelativeLayout.LayoutParams) adTitleView.getLayoutParams();

        adTitleParams.leftMargin = lMargin * AdClientContext.displayWidth / 1080;
        adTitleParams.rightMargin = rMargin * AdClientContext.displayWidth / 1080;
        adTitleParams.topMargin = textImgInner * AdClientContext.displayHeight / 1920;
        adTitleView.setLayoutParams(adTitleParams);

        adTitleView.setTextSize(fontSize);
        adTitleView.setTextColor(Color.parseColor(color));

    }

    private void setClickEventForSkipView(){
        if(!SdkHelper.isHit(adResponse.getResponseData().getCr())){
            adSkipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builderListener.onSkipClicked();
                }
            });
        }
    }

    public void start(){
        startBuildView();
    }

    private String color = "";
    private int fontSize;
    private boolean gif;
    /**
     * 这个变量控制是广告图片在上面还是广告标题在上面
     */
    private boolean imgUp;
    private int lMargin;
    private int rMargin;
    private int textImgInner;
    private int top;

    private void startBuildView() {
        final YdtAdBean.MetaGroupBean metaGroupBean = ydtAdBean.getMetaGroup().get(0);

        if(adResponse.getResponseData().isSelfRenderFillType()){ //信息流填充
            feedlistFillSplash(metaGroupBean);
        } else {
            //初始化广告的布局
            initFullView();
            setClickEventForSkipView();
            List<String> imgs = metaGroupBean.getImageUrl();
            if(imgs!=null&&imgs.size()>0){
                getBitmap(imgs.get(0));
            }

        }

    }

    /**
     * 信息流填充开屏
     * @param metaGroupBean
     */
    private void feedlistFillSplash(final YdtAdBean.MetaGroupBean metaGroupBean) {
        List<YdtAdBean> ydtAdBeanList = adResponse.getResponseData().getAds();
        Bitmap bitmap = adResponse.getClientRequest().getSplashBottomLogo();
        final Activity activity = adResponse.getClientRequest().getActivity();
        String backgroundImage = null;
        if (ydtAdBeanList != null && ydtAdBeanList.size() > 0){
            if (bitmap!=null && ydtAdBeanList.get(0).getHalf()!=null){
                YdtAdBean.HalfBean halfBean = ydtAdBeanList.get(0).getHalf();
                backgroundImage = halfBean.getUrl();
                color = halfBean.getColor();
                fontSize = halfBean.getFontSize();
                gif = halfBean.isGif();
                imgUp = halfBean.isImgUp();
                lMargin = halfBean.getLMargin();
                rMargin = halfBean.getRMargin();
                textImgInner = halfBean.getTextImgInner();
                top = halfBean.getTop();
            }else if (ydtAdBeanList.get(0).getFull() != null){
                YdtAdBean.FullBean fullBean = ydtAdBeanList.get(0).getFull();
                backgroundImage = fullBean.getUrl();
                color = fullBean.getColor();
                fontSize = fullBean.getFontSize();
                gif = fullBean.isGif();
                imgUp = fullBean.isImgUp();
                lMargin = fullBean.getLMargin();
                rMargin = fullBean.getRMargin();
                textImgInner = fullBean.getTextImgInner();
                top = fullBean.getTop();
            }
        }

        if (PublicUtils.isEmpty(backgroundImage)){
            builderListener.onError();
            return;
        }

        //这个变量控制是广告图片在上面还是广告标题在上面
        if (imgUp){
            initViewImageTop();
        }else{
            initViewTextTop();
        }

        justViewMargin();

        setClickEventForSkipView();

        HttpUtils.getImage(backgroundImage, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
                builderListener.onError();
            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                final Bitmap bgBitmap = BitmapFactory.decodeStream(stream);
                if(splashRootLayout != null && activity != null && bgBitmap != null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            splashRootLayout.setBackground(new BitmapDrawable(activity.getResources(), bgBitmap));
                            getAdLogo(ydtAdBean.getAdlogo());
                            List<String>  imgs = metaGroupBean.getImageUrl();
                            if(imgs!=null&&imgs.size()>0){
                                getBitmap(imgs.get(0));
                            }else{
                                builderListener.onError();
                            }
                        }
                    });
                }else{
                    builderListener.onError();
                }
            }
        });
    }

    /**
     * 请求广告位图片
     * */
    public void getBitmap( String imageUrl){
        if (PublicUtils.isEmpty(imageUrl)){
            builderListener.onError();
            return;
        }

        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
                builderListener.onError();
            }

            @Override
            public void onSuccess(InputStream stream) {
                // 获得bitmap对象
                adBitmap = BitmapFactory.decodeStream(stream);
                // 重新设置图片大小
                adBitmap = resizeAdImage(adBitmap);

                show();
            }
        });
    }

    /**
     * 请求成功，将广告进行展示
     * */
    private void show() {

        final YdtAdBean.MetaGroupBean metaGroupBean = ydtAdBean.getMetaGroup().get(0);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.addContentView(splashRootLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                appLogoBitmap = adResponse.getClientRequest().getSplashBottomLogo();

                if (appLogoBitmap!=null){
                    appBitmapView.setVisibility(View.VISIBLE);
                    appBitmapView.setImageBitmap(resizeAppLogoImage(appLogoBitmap));
                }
                if (adResponse.getResponseData().isSelfRenderFillType()){
                    //广告标题
                    String title = metaGroupBean.getAdTitle();
                    if(!PublicUtils.isEmpty(title)){
                        adTitleView.setText(title);
                        adTitleView.setVisibility(View.VISIBLE);
                    }

                    //广告图
                    adBitmapView.setBackground(new BitmapDrawable(activity.getResources(), adBitmap));
                    adBitmapView.setVisibility(View.VISIBLE);

                }else if(adResponse.getResponseData().isTemplateFillType()){
                    adBitmapView.setBackground(new BitmapDrawable(activity.getResources(), adBitmap));
                }

                builderListener.onShow(SplashViewBuilder.this);

            }
        });
    }


    /**
     *请求广告logo
     * */
    public void getAdLogo(String imageUrl){
        if (PublicUtils.isEmpty(imageUrl)){
            return;
        }
        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onSuccess(InputStream stream) {
                final Bitmap adLogoBitmap = BitmapFactory.decodeStream(stream);
                if (adLogoBitmap!=null){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PublicUtils.showImage(activity,adLogoView,adLogoBitmap);
                            adTextLogoView.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }

    /**
     * 下游logo进行等比例缩放
     * */
    private Bitmap resizeAppLogoImage(Bitmap bitmap) {
        // 获得bitmap的宽
        int width = bitmap.getWidth();
        // 获得bitmap的高
        int height = bitmap.getHeight();
        float scaleWidth=(float) AdClientContext.displayWidth / width;

        adHeight = AdClientContext.displayWidth - (int)scaleWidth * height;

        Matrix matrix = new Matrix();
        // 缩放图片
        matrix.postScale(scaleWidth, scaleWidth);
        return  Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
    }

    private int adWidth;
    private int adHeight;

    /**
     *  调整广告图片的尺寸
     * @param adBitmap
     * @return
     */
    private Bitmap resizeAdImage(Bitmap adBitmap) {

        // 获得bitmap的宽
        int width = adBitmap.getWidth();
        // 获得bitmap的高
        int height = adBitmap.getHeight();
        if (adResponse.getResponseData().isSelfRenderFillType()){
            float scaleWidth;
            float scaleHeight;
            Logger.e("resizeAdImage",""+width+"   " +height);
            if ((float)width / height <= 1.1){
                int newWidth = AdClientContext.displayWidth - (lMargin+rMargin) * AdClientContext.displayWidth/1080;
                int newHeight = (int)(height*newWidth/(width*1.5));
                scaleWidth = (float) newWidth/width;
                scaleHeight = (float) newHeight/height;
                adWidth = newWidth;
                adHeight = newHeight;
            }else{
                int newWidth = AdClientContext.displayWidth - (lMargin+rMargin) * AdClientContext.displayWidth/1080;
                scaleWidth = (float) newWidth/width;
                scaleHeight = scaleWidth;
                adWidth = newWidth;
                adHeight = ((int)(height*newWidth/(float)width));

            }
            Matrix matrix = new Matrix();
            // 缩放图片
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(adBitmap,0,0,width,height,matrix,true);
        }else{
            adWidth = AdClientContext.displayWidth;
            if (adHeight <= 0){
                adHeight = AdClientContext.displayHeight;
            }
            float scaleWidth=(float) adWidth / width;
            float scaleHeight=(float) adHeight / height;
            Matrix matrix = new Matrix();
            // 缩放图片
            matrix.postScale(scaleWidth, scaleHeight);
            return  Bitmap.createBitmap(adBitmap,0,0,width,height,matrix,true);
        }
    }

    public StrategyRootLayout getSplashRootLayout() {
        return splashRootLayout;
    }

    public ImageView getAdBitmapView() {
        return adBitmapView;
    }

    public ImageView getAppBitmapView() {
        return appBitmapView;
    }

    public TextView getAdTitleView() {
        return adTitleView;
    }

    public ImageView getAdLogoView() {
        return adLogoView;
    }

    public TextView getAdTextLogoView() {
        return adTextLogoView;
    }

    public TextView getAdSkipView() {
        return adSkipView;
    }

    public int getAdWidth() {
        return adWidth;
    }

    public int getAdHeight() {
        return adHeight;
    }
}
