package com.analytics.sdk.view.handler.api.splash;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SkipViewCoundownTimer;
import com.analytics.sdk.service.AdErrorFactory;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdShowStrategy;
import com.analytics.sdk.service.ad.entity.ConfigBeans;
import com.analytics.sdk.view.activity.WebviewActivity;
import com.analytics.sdk.view.handler.common.SplashBasicHandler;
import com.analytics.sdk.view.strategy.StrategyRootLayout;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.List;

public class ApiSplashHandler extends SplashBasicHandler {

    @Override
    protected void onHandleAd(final AdResponse adResponse, AdListeneable clientAdListener, ConfigBeans configBeans) throws AdSdkException {

        final StrategyRootLayout splashStrategyView = (StrategyRootLayout) adResponse.getClientRequest().getAdContainer();

        List<AdShowStrategy> configWeiList = adResponse.getResponseData().getStrategyList();
        if (configWeiList != null && configWeiList.size() > 0) {
            final AdShowStrategy adShowStrategy = configWeiList.get(0);
            int interaction_type = adShowStrategy.getInteraction_type();

            if (interaction_type == IAdService.JMP) {//1 == 加载WebView 2 == 下载文件

                String[] images = adShowStrategy.getImgs();
                String imageUrl = images[0];

                ViewGroup adContainer = adResponse.getClientRequest().getAdContainer();
                final Activity activity = adResponse.getClientRequest().getActivity();
                final View view = activity.getLayoutInflater().inflate(R.layout.jhsdk_splash_with_api_ad_imageview, adContainer);

                final AQuery query = new AQuery(view);
                query.id(R.id.jhsdk_ad_view).image(imageUrl, false, true, 0, 0,
                        new BitmapAjaxCallback() {
                            @Override
                            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {

                                if (bm == null) {
                                    AdError adError = AdErrorFactory.factory().create(ErrorCode.Api.ERROR_NO_AD);
                                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR, adResponse, adError));
                                    return;
                                }

                                if (UIHelper.isActivityDestoryed(activity)) {
                                    AdError adError = AdErrorFactory.factory().create(ErrorCode.Api.ERROR_AD_CONTAINER_DESTORY);
                                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR, adResponse, adError));
                                    return;
                                }

                                if (iv.getVisibility() == View.VISIBLE) {

                                    iv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            cancelCountdown();

                                            String clickUrl = adShowStrategy.getClick_url();
                                            EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_CLICK, adResponse));

                                            WebviewActivity.startWebActivity(activity, "", clickUrl, new WebviewActivity.WebViewStateListener() {
                                                @Override
                                                public void onShow() {
                                                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE, adResponse));
                                                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS, adResponse));
                                                }
                                            });

                                        }
                                    });

                                    splashStrategyView.apply(adResponse);

                                    iv.setImageBitmap(bm);
                                    showAdClose(query, view);
                                }
                            }
                        });
            }
        }

    }

    private void cancelCountdown(){
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    SkipViewCoundownTimer countDownTimer = null;
    private void showAdClose(AQuery query, final View view) {
        EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_SHOW, adResponse));

        final TextView skipView = view.findViewById(R.id.jhsdk_ad_close);

        countDownTimer = new SkipViewCoundownTimer(skipView, new SkipViewCoundownTimer.OnFinishListener() {
            @Override
            public void onFinish() {
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE, adResponse));
                EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS, adResponse));
            }
        }, 5300, 1000);
        countDownTimer.start();

        query.id(R.id.jhsdk_ad_close).visibility(View.VISIBLE).clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ImageView imageView = view.findViewById(R.id.jhsdk_ad_view);
                Drawable drawable = imageView.getDrawable();

                Rect rect = drawable.getBounds();

                int width = rect.width();
                int height = rect.height();

                if (width > 1 && height > 1) {
                    cancelCountdown();
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_EXPOSURE, adResponse));
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_DISMISS, adResponse));
                } else {
                    AdError adError = AdErrorFactory.factory().create(ErrorCode.Api.ERROR_AD_IMAGE);
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_ERROR, adResponse, adError));
                }
            }
        });
    }
}
