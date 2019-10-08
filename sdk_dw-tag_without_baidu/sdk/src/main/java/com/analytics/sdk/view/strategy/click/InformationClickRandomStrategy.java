package com.analytics.sdk.view.strategy.click;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;

import com.analytics.sdk.R;
import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.AdViewExt;
import com.analytics.sdk.common.cache.LruCacheSimple;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.view.strategy.FeedsListFrameLayout;
import com.analytics.sdk.view.strategy.FeedsListFrameLayout2;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InformationClickRandomStrategy implements ViewTreeObserver.OnPreDrawListener{

    static final String TAG = "InformationClickRandomStrategy";

    private AdResponse adResponse;
    private AdViewExt adViewExt;

    public final static LruCacheSimple<String,AdView> adViewCache = new LruCacheSimple<>();

    public void apply(final AdView adNativeView, final AdResponse adResponse){

        if(adNativeView == null || adResponse == null){
            return;
        }

        this.adViewExt = (AdViewExt) adNativeView;
        this.adResponse = adResponse;

        adViewCache.put(adViewExt.getId(),adNativeView);

        adNativeView.getView().getViewTreeObserver().addOnPreDrawListener(this);

    }

    public void unapply(){
        if(!adViewExt.isRecycle()){
            adViewExt.getView().getViewTreeObserver().removeOnPreDrawListener(this);
        }
    }

    public static AdView isClickAdView(int dx, int dy){
//        List<AdView> noShownList = new ArrayList<>();
        AdView hitView = null;

        LinkedHashMap<String,AdView> map = adViewCache.snapshot();

        for(Iterator<Map.Entry<String,AdView>> iter = map.entrySet ().iterator();iter.hasNext();){
            Map.Entry<String,AdView> me = iter.next();

            String keyId = me.getKey();
            AdView adView = adViewCache.get(keyId);

            if(adView.getView() == null){
                continue;
            }

            Rect adViewVisibleRect = new Rect();
            boolean visibleRect = adView.getView().getGlobalVisibleRect(adViewVisibleRect);

            if(adView.getView().isShown() && visibleRect){
                if(adViewVisibleRect.contains(dx,dy)){
                    hitView = adView;
                    break;
                }
            }
        }

        return (hitView);
    }

    private ViewParent findListView(View view){
        ViewParent viewParent = view.getParent();
        if(viewParent != null) {
            if(viewParent instanceof ListView){
                return viewParent;
            } else if(viewParent instanceof AbsListView){
                return viewParent;
            } else if(viewParent instanceof RecyclerView) {
                return viewParent;
            } else {
                if(viewParent instanceof View){
                    return findListView((View)viewParent);
                }
            }
        }
        return null;
    }

    @Override
    public boolean onPreDraw() {

        final View realAdView = adViewExt.getView();
        if(realAdView == null){
            realAdView.getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
        }

        if(adViewExt.isRecycle()){
            adViewExt.getView().getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
        }

        int vW = realAdView.getWidth();
        int vH = realAdView.getHeight();

        if(vW > 1 && vH > 1){
            realAdView.getViewTreeObserver().removeOnPreDrawListener(this);
        } else {
            return true;
        }


        ViewParent viewParent = findListView(adViewExt.getView());

        Logger.i(TAG,"onPreDraw viewParent = " + viewParent);

        viewParent = null;

        if(viewParent != null){
            if(viewParent instanceof ListView){
                Logger.i(TAG,"parent is listview");
                ListView listView = (ListView) viewParent;
                ViewParent listViewParent = listView.getParent();

                if(listViewParent != null && listViewParent instanceof ViewGroup && !(listViewParent instanceof FeedsListFrameLayout)){

                    ViewGroup listViewGroup = (ViewGroup) listViewParent;
                    listViewGroup.removeView(listView);

                    LayoutInflater li = LayoutInflater.from(((ListView) viewParent).getContext());
                    View view = li.inflate(R.layout.jhsdk_feedlist_click_strategy_layout,listViewGroup);
                    FeedsListFrameLayout frameLayout = view.findViewById(R.id.feedlist_parent);
                    frameLayout.setAdRequest(adResponse);
                    frameLayout.addView(listView);

                }

            } else if(viewParent instanceof RecyclerView){
                Logger.i(TAG,"parent is recyclerview");

                RecyclerView recyclerView = (RecyclerView) viewParent;
                ViewParent recyclerViewParent = recyclerView.getParent();

                if(recyclerViewParent != null && recyclerViewParent instanceof ViewGroup && !(recyclerViewParent instanceof FeedsListFrameLayout)){

                    ViewGroup recyclerViewGroup = (ViewGroup) recyclerViewParent;

                    ((ViewGroup) recyclerViewParent).removeView(recyclerView);

                    LayoutInflater li = LayoutInflater.from(((RecyclerView) viewParent).getContext());

                    View view = li.inflate(R.layout.jhsdk_feedlist_click_strategy_layout,recyclerViewGroup);

                    FeedsListFrameLayout frameLayout = view.findViewById(R.id.feedlist_parent);
                    frameLayout.setAdRequest(adResponse);
                    frameLayout.addView(recyclerView);

                }

                Rect rect = new Rect();
                boolean isShow = adViewExt.getView().getGlobalVisibleRect(rect);

                Logger.i("ClickRandomDebugHelper2","ClickRandomDebugHelper2 parent is recyclerview , isShow = " + isShow);
//                new ClickRandomDebugHelper2().apply((FrameLayout)adViewExt.getView(),rect);

            } else if(viewParent instanceof ViewGroup){

                Logger.i(TAG,"parent is ViewGroup");

                ViewGroup viewParent2 = (ViewGroup) adViewExt.getView().getParent();

                ViewParent viewParent2Parent = viewParent2.getParent();

                if(viewParent != null  && !(viewParent instanceof FeedsListFrameLayout)){

                    ViewGroup newParent = (ViewGroup) viewParent2Parent;

                    ((ViewGroup) viewParent2Parent).removeView(viewParent2);

                    LayoutInflater li = LayoutInflater.from(viewParent2.getContext());

                    View view = li.inflate(R.layout.jhsdk_feedlist_click_strategy_layout,newParent);

                    FeedsListFrameLayout frameLayout = view.findViewById(R.id.feedlist_parent);
                    frameLayout.setAdRequest(adResponse);

                    frameLayout.addView(viewParent2);

                }

            }
        } else {
            Logger.i(TAG,"parent is null");

            View view = adViewExt.getView();
            Logger.i(TAG, "apply view name = " + view);

            if(view != null){

                boolean isShown = view.isShown();
                boolean isVisible = view.getGlobalVisibleRect(new Rect());

                Logger.i(TAG, "apply isShown = " + isShown + " , isVisible = " + isVisible);

                appendStrategyView(adResponse);

            }

            return false;

        }
        return true;
    }

    private void add(ViewParent viewParent){
        viewParent = adViewExt.getView().getParent();

        if(viewParent instanceof ViewGroup){

            Logger.i(TAG,"parent is ViewGroup");

            ViewGroup viewParent2 = (ViewGroup) viewParent;

            ViewParent viewParent2Parent = viewParent2.getParent();

            if(viewParent != null  && !(viewParent instanceof FeedsListFrameLayout)){

                Logger.i(TAG,"parent reset");

                ViewGroup newParent = (ViewGroup) viewParent2Parent;

                ((ViewGroup) viewParent2Parent).removeView(viewParent2);

                LayoutInflater li = LayoutInflater.from(viewParent2.getContext());

                View view = li.inflate(R.layout.jhsdk_feedlist_click_strategy_layout,newParent);

                FeedsListFrameLayout frameLayout = view.findViewById(R.id.feedlist_parent);
                frameLayout.setAdRequest(adResponse);

                frameLayout.addView(viewParent2);

            }

        }
    }

    private FeedsListFrameLayout2 appendStrategyView(AdResponse adResponse){
        Activity activity = adResponse.getClientRequest().getActivity();
        ViewGroup androidContentView = activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);

        //开发者的view
        View devView = androidContentView.getChildAt(0);
        Logger.i(TAG, "dev view name = " + devView.getClass().getName());
        if(devView != null && devView instanceof FeedsListFrameLayout2) {
            Logger.i(TAG, "exist appender view");
            apply(adViewExt.getView(),(FeedsListFrameLayout2) devView,adResponse);
            return (FeedsListFrameLayout2) devView;
        }

        androidContentView.removeAllViews();

        LayoutInflater li = LayoutInflater.from(androidContentView.getContext());

        View view = li.inflate(R.layout.jhsdk_feedlist_click_strategy_layout2,androidContentView);

        final FeedsListFrameLayout2 frameLayout = view.findViewById(R.id.feedlist_parent);
        frameLayout.setAdRequest(adResponse);

        //向开发者view添加一层
        frameLayout.addView(devView);

        apply(adViewExt.getView(),frameLayout,adResponse);

        return frameLayout;
    }

    public boolean apply(final View adView,final FeedsListFrameLayout2 frameLayout,final AdResponse adResponse) {

        try {

            ThreadExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Rect rect = new Rect();
                        adView.getGlobalVisibleRect(rect);

                        int[] xy = new int[2];
                        adView.getLocationInWindow(xy);

                        int[] xy1 = new int[2];
                        adView.getLocationOnScreen(xy1);

                        Logger.i(TAG,"rect1 = " + rect);
                        Logger.i(TAG,"rect2 = " + xy[0] + " , y = " + xy[1]);
                        Logger.i(TAG,"rect2 = " + xy1[0] + " , y = " + xy1[1]);

                        int marginTop = AdClientContext.statusBarHeight;

                        if(UIHelper.isFullScreen(adResponse.getClientRequest().getActivity())){
                            marginTop = 0;
                        }

                        Logger.i(TAG,"marginTop = " + marginTop);

                        rect = new Rect(rect.left,rect.top - marginTop,rect.right,rect.bottom - marginTop);

                        frameLayout.adView = adView;

                        frameLayout.setRect(rect);

                        if(AdConfig.getDefault().isPrintLog()){
                            View view = frameLayout.findViewWithTag("debug");
                            if(view != null){
                                frameLayout.removeView(view);
                            }
                            new ClickRandomDebugHelper2().apply(frameLayout,rect,adResponse);
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            },500);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
