package com.adsdk.demo.feedlist;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.adsdk.demo.GlobalConfig;
import com.adsdk.demo.LogControl;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.LayoutStyle;
import com.analytics.sdk.client.ViewStyle;
import com.analytics.sdk.client.feedlist.AdSize;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.client.feedlist.FeedListAdListener;
import com.analytics.sdk.view.strategy.helper.ReflectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FeedListActivity extends Activity implements FeedListAdListener, RecyclerViewMoreUtil.RefreshDataListener {
    private static final String TAG = FeedListActivity.class.getSimpleName();
    public static final int MAX_ITEMS = 20;
    public static final int AD_COUNT  = 5;    // 加载广告的条数，取值范围为[1, 10]
    public int FIRST_AD_POSITION = 1; // 第一条广告的位置
    public int ITEMS_PER_AD = 3;     // 每间隔10个条目插入一条广告

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomAdapter mAdapter;
    private List<NormalItem> mNormalDataList = new ArrayList<NormalItem>();
    private List<NormalItem> newsList=new ArrayList<>();
    private List<AdView> mAdViewList;
    private HashMap<AdView, Integer> mAdViewPositionMap = new HashMap<>();
    private RecyclerViewMoreUtil util;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_LAYOUT_ID);
        util = new RecyclerViewMoreUtil();
        mRecyclerView = findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ID);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CustomAdapter(mNormalDataList);
        mRecyclerView.setAdapter(mAdapter);
        util.init(this, mRecyclerView, mAdapter, this);
        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        FIRST_AD_POSITION = 0;

        // 使用完了每一个NativeExpressADView之后都要释放掉资源。
        for(Iterator<Map.Entry<AdView, Integer>> iter = mAdViewPositionMap.entrySet ().iterator();iter.hasNext();) {
            Map.Entry<AdView, Integer> me = iter.next();
            AdView adView = me.getKey();
            adView.recycle();
        }
    }

    private void initData() {
        for (int i = 0; i < MAX_ITEMS; ++i) {
            mNormalDataList.add(new NormalItem("No." + i + " Normal Data"));
            newsList.add(new NormalItem("No." + i + " Normal Data"));
        }
        mAdapter.addItem(newsList);
        newsList.clear();
        requestFeedList();
    }

    private void requestFeedList() {
//        AdSize adSize = new AdSize(AdSize.FULL_WIDTH, UIHelper.dip2px(this,58)); // 消息流中用AUTO_HEIGHT
        AdSize adSize = new AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
        LogControl.i(TAG,"loadInformationFlow enter");

        ViewStyle titleStyle = ViewStyle.obtain()
                                            .setTextSize(12)
                                            .setTextColor(Color.RED);
//                                            .setBgColor(Color.BLACK);

//        ViewStyle descStyle = ViewStyle.obtain(titleStyle);

        LayoutStyle layoutStyle = LayoutStyle.obtain()
                                                .setHiddenClose(false)
                                                .setBgColor(Color.TRANSPARENT)
                                                .addViewStyle(ViewStyle.STYLE_TITLE,titleStyle);
//                                                .addViewStyle(ViewStyle.STYLE_DESC,descStyle);

        adRequest = new AdRequest.Builder(this)
                                            .setCodeId(GlobalConfig.ChannelId.FEED_LIST)
                                            .setAdRequestCount(AD_COUNT)
                                            .setAdSize(adSize)
                                            .setLayoutStyle(layoutStyle)
                                            .build();

        adRequest.loadFeedListAd(this);

    }

    @Override
    public boolean loadMore() {
        initData();
        return true;
    }

    @Override
    public void onAdError(AdError adError) {
        LogControl.i(TAG,"onAdError enter , adError = "+adError);
    }

    @Override
    public void onAdLoaded(final List<AdView> adList) {
        LogControl.i(TAG,"onAdLoaded enter , list size = " + adList);
        mAdViewList = adList;
        LogControl.i(TAG,"onAdLoaded enter , size = "+mAdViewList.size());
        for (int i = 0; i < mAdViewList.size(); i++) {
            int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
            if (position < mNormalDataList.size()) {
                AdView view = mAdViewList.get(i);

                mAdViewPositionMap.put(view, position); // 把每个广告在列表中位置记录下来
                mAdapter.addADViewToPosition(position, mAdViewList.get(i));
            }
        }
        FIRST_AD_POSITION=mNormalDataList.size();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAdClicked(AdView adView) {
        LogControl.i(TAG,"onAdClicked enter");
    }

    @Override
    public void onAdDismissed(AdView adView) {
        LogControl.i(TAG,"onAdDismissed enter");
        if (mAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            mAdapter.removeADView(removedPosition, adView);
        }
    }

    @Override
    public void onADExposed(AdView adView) {
        LogControl.i(TAG,"onADExposed enter");
    }

    @Override
    public void onVideoLoad() {
        LogControl.i(TAG,"onVideoLoad enter");
    }

    @Override
    public void onVideoPause() {
        LogControl.i(TAG,"onVideoPause enter");
    }

    @Override
    public void onVideoStart() {
        LogControl.i(TAG,"onVideoStart enter");
    }

    public class NormalItem {
        private String title;

        public NormalItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


    /** RecyclerView的Adapter */
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;
        private List<Object> mData;

        public CustomAdapter(List list) {
            mData = list;
        }

        // 把返回的NativeExpressADView添加到数据集里面去
        public void addADViewToPosition(int position, AdView adView) {
            if (position >= 0 && position < mData.size() && adView != null) {
                mData.add(position, adView);
            }
        }

        // 移除ADView的时候是一条一条移除的
        public void removeADView(int position, AdView adView) {
            mData.remove(position);
            mAdapter.notifyItemRemoved(position); // position为adView在当前列表中的位置
            mAdapter.notifyItemRangeChanged(0, mData.size() - 1);
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position) instanceof AdView ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
            int type = getItemViewType(position);
            if (TYPE_AD == type) {

                final AdView adView = (AdView) mData.get(position);

                mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
                if (customViewHolder.container.getChildCount() > 0
                        && customViewHolder.container.getChildAt(0) == adView) {
                    adView.render();
                    return;
                }

                if (customViewHolder.container.getChildCount() > 0) {
                    customViewHolder.container.removeAllViews();
                }

                if (adView.getView().getParent() != null) {
                    ((ViewGroup) adView.getView().getParent()).removeView(adView.getView());
                }

                customViewHolder.container.addView(adView.getView());
                adView.render(); // 调用render方法后sdk才会开始展示广告
            } else {
                customViewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_AD_ID : GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_ITEM_NORMAL_ID;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        public void addItem(List<NormalItem> list){
            mData.addAll(list);
            notifyDataSetChanged();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public TextView title;
            public ViewGroup container;

            public CustomViewHolder(View view) {
                super(view);
                title = view.findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_TITLE_ID);
                container = view.findViewById(GlobalConfig.RConfig.FEEDLIST_ACTIVITY_RECYCLER_VIEW_AD_CONTAINER_ID);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(),title.getText().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

}
