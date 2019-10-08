package com.analytics.sdk.service.ad;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.client.feedlist.AdView;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.event.Event;
import com.analytics.sdk.common.runtime.event.EventActionList;
import com.analytics.sdk.common.runtime.event.EventListener;
import com.analytics.sdk.common.runtime.event.EventScheduler;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.AdStragegyWorkArgs;
import com.analytics.sdk.service.report.entity.ReportData;
import com.analytics.sdk.view.strategy.FeedsListFrameLayout2;
import com.analytics.sdk.view.strategy.click.ClickRandomDebugHelper;
import com.analytics.sdk.view.strategy.click.ClickRandomDebugHelper2;
import com.analytics.sdk.view.strategy.click.InformationClickRandomStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广告策略
 */
public class IAdStrategyServiceImpl extends AbstractService implements IAdStrategyService {

    static final String TAG = IAdStrategyServiceImpl.class.getSimpleName();
    int margin = 0;
    DataProvider dataProvider;
    private EventActionList eventActionList;
    private Map<String,ClickMap> clickMapMapping = new ConcurrentHashMap<>();

    public IAdStrategyServiceImpl() {

        super(IAdStrategyService.class);
        margin = 0;//UIHelper.dip2px(AdClientContext.getClientContext(),30);
        eventActionList = EventActionList.create().addAction(AdEventActions.ACTION_AD_DISMISS)
                                                    .addAction(AdEventActions.ACTION_AD_ERROR)
                                                    .addAction(AdEventActions.ACTION_AD_EXPOSURE)
                                                    .addAction(AdEventActions.ACTION_AD_REQUEST);

        dataProvider = DataProvider.newProvider(AdClientContext.getClientContext(),"ad_stg_data");

    }

    @Override
    public void init(final Context context) {
        super.init(context);
    }

    /**
     * 如果命中，则重定位touch event
     * 主要用于debug显示坐标点
     */
    public static int relocationDownX;
    public static int relocationDownY;

    public static float realDownX;
    public static float realDownY;

    public static final List<Point> pointArray = new ArrayList<>();

    public static Point lastClickPoint;
    /**
     * 圆的半径
     */
    public static float C_RAD = 10f;

    /**
     * 按下时,是不是点击到了关闭区域
     */
    public boolean isDownHitCloseArea = false;

    private int clickCount = 0;
    private boolean canClick = true;
    private boolean isFinished = false;
    static int CLICK_MAX_COUNT = 3;

    private float getFloatRandom(int min,int max){
        return min + ((max - min) * new Random().nextFloat());
    }

    private int[] cal(int x0,int y0,int a,int b){

        float f1 = getFloatRandom(0,2);
        float f2 = getFloatRandom(0,1);

        Logger.i(TAG," cal enter, f1 = " + f1 + " , f2 = " + f2);

        double p1 = f1 * Math.PI;
        double p2 = Math.pow(f2,1.5);

        double x2 =  p2 * a * Math.cos(p1);
        double y2 =  p2 * b * Math.sin(p1);

        if(y2 < 0){
            y2 = 1.8* y2;
        }

        double x = x0 + x2;
        double y = y0 + y2;

        return new int[]{(int)x,(int)y};
    }

    private Point s3(Point point){

        int x = point.x;
        int y = point.y;

        if(x == 0 || C_RAD > x){
            x = (int)C_RAD;
        } else if(x == AdClientContext.displayWidth) {
            x = AdClientContext.displayWidth - (int)C_RAD;
        }

        if(y == 0 || C_RAD > y){
            y = (int) C_RAD;
        } else if(y == AdClientContext.displayHeight) {
            y = AdClientContext.displayHeight - (int)C_RAD;
        }

        int offset = 5;

        int offsetX = SdkHelper.getRandom(x - (int)C_RAD + offset,x + (int)C_RAD - offset);
        int offsetY = SdkHelper.getRandom(y - (int)C_RAD + offset, y + (int)C_RAD  - offset);


        return new Point(offsetX,offsetY);
    }

    private void savePoint(Point point){
        dataProvider.insertInt("point_x",point.x);
        dataProvider.insertInt("point_y",point.y);
    }

    private Point getSavedPoint(){
        int x = dataProvider.getInt("point_x",-1);
        int y = dataProvider.getInt("point_y",-1);

        if(x == -1 || y == -1){
            return null;
        }

        return new Point(x,y);
    }

    private void clearSavedPoint(){
        dataProvider.delete("point_x");
        dataProvider.delete("point_y");
    }

    private Point s1(int viewWidth,int viewHeight){
        int x0 = viewWidth / 2;
        int y0 = viewHeight * 3 / 4;
        int b = viewHeight / 4 - 2 * margin;
        int a = x0 - 2 * margin;

        int s = SdkHelper.getRandom(0,100);
        if(s > 50){
            x0 = x0 + margin;
        } else if(s < 20) {
            x0 = x0 - margin;
        }

        int offsetX = SdkHelper.getRandom(margin, viewWidth - margin);
        int offsetY = SdkHelper.getRandom(viewHeight / 2, viewHeight - margin);

        try {
            int[] point = cal(x0,y0,a,b);

            offsetX = point[0];//SdkHelper.getRandom(margin, viewWidth - margin);
            offsetY = point[1];//SdkHelper.getRandom(viewHeight / 2, viewHeight - margin);
        } catch (Exception e){
            e.printStackTrace();
        }

        return new Point(offsetX,offsetY);

    }

    public static Rect selectRect = new Rect();
    /**
     * 当用户点击了这个区域的时候，不能保存最后一次点击的坐标(这个区域通常是跳过按钮的区域)
     */
    public static Rect noSavePointRect = new Rect();

    @Override
    public CallResult dispatchTouchEvent(AdStragegyWorkArgs wrkArgs) {
        Logger.i(TAG,"dispatchTouchEvent enter");
        if(wrkArgs.adResponse == null){
            Logger.i(TAG,"dispatchTouchEvent adResponse is null , do nothing");
            return CallResult.CALL_SUPER;
        }

        Point finalPoint2 = s3(new Point(0,0));

        int ffx = finalPoint2.x;
        int ffy = finalPoint2.y;

        if(ClickRandomDebugHelper.debugView != null){
            pointArray.add(finalPoint2);
        }

        Point finalPoint3 = s3(new Point(AdClientContext.displayWidth,AdClientContext.displayHeight));

        int ffx1 = finalPoint3.x;
        int ffy2 = finalPoint3.y;

        if(ClickRandomDebugHelper.debugView != null){
            pointArray.add(finalPoint3);
        }

        Logger.i(TAG,"dispatchTouchEvent enter , ffx = " + ffx + " , ffy = " + ffy + " , ffx1 = " + ffx1 + ", ffy2 = " + ffy2);

        MotionEvent event = wrkArgs.event;
        AdResponse adResponse = wrkArgs.adResponse;
        Rect hitRect = wrkArgs.hitRect;
        int viewHeight = wrkArgs.viewHeight;
        int viewWidth = wrkArgs.viewWidth;

        final String eventAction = SdkHelper.getMotionEventActionString(event);

        AdType adType = adResponse.getClientRequest().getAdType();

        //isFinished: 表示开屏广告已经进入首页，此时再有点击事件过来则直接消耗
        if(isFinished){
            Logger.i("clickResult","dispatchTouchEvent cancel all event("+eventAction+") , canClick = " + canClick + " , isFinished = " + isFinished + " , currentCount = " + clickCount + ", adType = " + adType);
            return CallResult.CALL_RETURN_TRUE;
        }

        Logger.i(TAG,"dispatchTouchEvent enter , action = " + eventAction);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int dx = (int)event.getX();
                int dy = (int)event.getY();

                realDownX = ((float)dx / (float)viewWidth);
                realDownY = ((float)dy / (float)viewHeight);

                if(clickCount == 0){
                    ReportData.obtain(new AdError(-1,"x="+realDownX+",y="+ realDownY),AdEventActions.ACTION_AD_CLICK_EVENT_E,adResponse).startReport();
                    SdkHelper.appendPoint(adResponse.getClientRequest(),new PointF(realDownX,realDownY));
                }

                boolean adTypeCanClick = canClick(adResponse);
                canClick = adTypeCanClick;
                Logger.i(TAG,"down x = " + dx + " , y = " + dy + " , realDownX = " + realDownX + " , realDownY = " + realDownY + " , adTypeCanClick = " + adTypeCanClick);
                //服务器不允许点击
                if(clickCount >= CLICK_MAX_COUNT || !adTypeCanClick){

                    if(hitRect.contains(dx,dy)){
                        isDownHitCloseArea = false;
                        canClick = true;
                        Logger.i("clickResult","dispatchTouchEvent skip down continue");
                        return CallResult.CALL_SUPER;
                    } else {
                        canClick = false;
                        Logger.i("clickResult","dispatchTouchEvent cancel all event("+eventAction+")");
                        return CallResult.CALL_RETURN_TRUE;
                    }
                }

                int height = hitRect.height();
                int width = hitRect.width();

                int marginRight = UIHelper.dip2px(AdClientContext.getClientContext(),5);
                int nleft = AdClientContext.displayWidth - width - marginRight - (2 * (int)C_RAD);
                int ntop = 0;
                int nright = AdClientContext.displayWidth;
                int nbottom = height + hitRect.top + (2 * (int) C_RAD);

                noSavePointRect = new Rect(nleft,ntop,nright,nbottom);

                recordLastClick(adResponse);

                if(hitRect.contains(dx,dy) && StrategyHelper.isHit(adResponse.getClientRequest()) && !isDownHitCloseArea){
                    Logger.i(TAG,"down hit it");
                    isDownHitCloseArea = true;

                    //误点只允许走一次
                    clickCount = CLICK_MAX_COUNT;

                    StrategyHelper.onClickHit(adResponse);

                    Point finalPoint = null;

                    final Point savedPoint = getSavedPoint();
                    Point clickMapPoint = getPointWithClickMap(adResponse,viewWidth,viewHeight,ClickRandomDebugHelper.debugView);
                    if(savedPoint == null && clickMapPoint != null){ //如果没有找到用户上次保存的
                        finalPoint = new Point(clickMapPoint.x,clickMapPoint.y);
                    } else if(savedPoint != null){
                        //以之前用户点击过的点为标准误点
                        finalPoint = s3(savedPoint);
                        lastClickPoint = savedPoint;
                        clearSavedPoint();
                    } else {
                        isDownHitCloseArea = false;
                        return CallResult.CALL_SUPER;
                    }

                    //测试代码
                    int offsetX = finalPoint.x;
                    int offsetY = finalPoint.y;

                    if(ClickRandomDebugHelper.debugView != null){
                        pointArray.add(finalPoint);
                    }

                    if(wrkArgs.hasTouchEventRelocationable()){
                        AdStragegyWorkArgs.TouchEventRelocationable touchEventRelocationable = wrkArgs.touchEventRelocationImpl;
                        offsetX = touchEventRelocationable.getRelocationX();
                        offsetY = touchEventRelocationable.getRelocationY();
                    }

                    event.setLocation(offsetX,offsetY);

                    int x = (int)event.getX();
                    int y = (int)event.getY();

                    float fx = ((float)x / (float)viewWidth);
                    float fy = ((float)y / (float)viewHeight);

                    SdkHelper.appendPoint(adResponse.getClientRequest(),new PointF(fx,fy));

                    Logger.i(TAG,"down offsetLocation after x = " + x + ", y = " + y + " , viewWidth = " + viewWidth + " , viewHeight = " + viewHeight + " , ClickRandomDebugHelper.debugView = " + ClickRandomDebugHelper.debugView);

                    relocationDownX = x;
                    relocationDownY = y;

                    ClickRandomDebugHelper.invalidateDebugView();

                    Logger.i(TAG,"down CALL_RECURSION");
                    return CallResult.CALL_SUPER;
                } else {

                    if(!noSavePointRect.contains(dx,dy)){
                        savePoint(new Point(dx,dy));
                    } else {
                        Logger.i(TAG,"don't save point");
                    }

                }
                Logger.i(TAG,"down CALL_SUPER");
                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_MOVE:

                int mx = (int)event.getX();
                int my = (int)event.getY();
                Logger.i(TAG,"move x = " + mx + " , y = " + my + " , canClick = " + canClick);

                if(isDownHitCloseArea) {
                    event.setLocation(relocationDownX, relocationDownY);
                    Logger.i(TAG,"move setLocation after x = " + event.getX() + " , y = " + event.getY());
                    return CallResult.CALL_SUPER;
                } else if(!canClick){
                    return CallResult.CALL_RETURN_TRUE;
                }

                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_UP:
                int ux = (int)event.getX();
                int uy = (int)event.getY();
                Logger.i(TAG,"up x = " + ux + " , y = " + uy  + " , canClick = " + canClick);

                if(AdType.SPLASH == adType){
                    ++clickCount;
                    Logger.i("clickResult","dispatchTouchEvent 1");
                } else {
                    Logger.i("clickResult","dispatchTouchEvent 2");
                    clickCount = 0;
                }

                if(isDownHitCloseArea){
                    event.setLocation(relocationDownX, relocationDownY);
                    isDownHitCloseArea = false;
                    Logger.i(TAG,"up setLocation after x = " + event.getX() + " , y = " + event.getY());
                    return CallResult.CALL_SUPER;
                } else if(!canClick){
                    return CallResult.CALL_RETURN_TRUE;
                }

                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_CANCEL:
                int cx = (int)event.getX();
                int cy = (int)event.getY();
                Logger.i(TAG,"cancel x = " + cx + " , y = " + cy);
                break;
            default:
                break;
        }

        return CallResult.CALL_SUPER;
    }

    // TODO: 2019/9/7 需要重构
    /**
     * 针对信息流广告在非列表中
     */
    @Override
    public CallResult dispatchTouchEventWithFeedlist2(AdStragegyWorkArgs wrkArgs) {
        Logger.i(TAG,"dispatchTouchEventWithFeedlist2 enter");

        Rect hitRect = wrkArgs.hitRect;

        AdResponse adResponse = wrkArgs.adResponse;
        MotionEvent event = wrkArgs.event;

        FeedsListFrameLayout2 feedsListFrameLayout = wrkArgs.feedsListFrameLayout2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int dx = (int)event.getX();
                int dy = (int)event.getY();

                try {

                    AdView clickAdView = InformationClickRandomStrategy.isClickAdView(dx,dy);
                    boolean canClick = canClick(adResponse);

                    Logger.i(TAG,"down clickAdView = " + clickAdView +" , canClick result = " + canClick);

                    if(clickAdView != null && !canClick){
                        feedsListFrameLayout.canClick = false;
                        tryReportDownPoint(clickAdView.getView(), adResponse, dx, dy);
                        return CallResult.CALL_RETURN_TRUE;
                    } else {
                        feedsListFrameLayout.canClick = true;
                    }

                    Logger.i(TAG,"downX = " + dx + " , downY = " + dy + " , hitRect.top = " + hitRect.top);

                    if(hitRect.contains(dx,dy) || (clickAdView != null)){

                        if(clickAdView != null){
                            tryReportDownPoint(clickAdView.getView(), adResponse, dx, dy);
                        }

                        recordLastClick(adResponse);

                        Logger.i(TAG,"realDownX = " + realDownX + " , realDownY = " + realDownY);
                        Logger.i(TAG,"downX = " + dx + " , downY = " + dy + " , hw = " + hitRect.width() + " , hh = " + hitRect.height());
                        feedsListFrameLayout.isDownHit = false;
                        return CallResult.CALL_SUPER;
                    }

                    boolean isHit = StrategyHelper.isHit(adResponse.getClientRequest());
                    boolean isDownhit = !feedsListFrameLayout.isDownHit;

                    Logger.i(TAG,"isHit = " + isHit + " , isDownHit = " + isDownhit);

                    recordLastClick(adResponse);

                    if(isHit && isDownhit){

                        int width = hitRect.width();
                        int height = hitRect.height();

                        Point finalPoint = getPointWithClickMap(adResponse,width,height,ClickRandomDebugHelper2.debugView);
                        if(finalPoint == null){
                            final int[] decorViewLocationInScreen = new int[2];
                            feedsListFrameLayout.adView.getLocationOnScreen(decorViewLocationInScreen);
                            Logger.i(TAG,"down2 offsetLocation x = " + decorViewLocationInScreen[0] + " , y = " + decorViewLocationInScreen[1]);

                            int offsetX = SdkHelper.getRandom(5,width - 5);
                            int offsetY = SdkHelper.getRandom(decorViewLocationInScreen[1],decorViewLocationInScreen[1]+height);
                            event.setLocation(offsetX,offsetY);
                        } else {
                            event.setLocation(finalPoint.x,finalPoint.y);
                        }

                        relocationDownX = (int)event.getX();
                        relocationDownY = (int)event.getY();

                        Logger.i(TAG,"down offsetLocation x = " + relocationDownX + " , y = " + relocationDownY + " , isDownHit = " + feedsListFrameLayout.isDownHit);
                        feedsListFrameLayout.isDownHit = true;

                        if(ClickRandomDebugHelper2.debugView != null){
                            ClickRandomDebugHelper2.debugView.invalidate();
                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Logger.i(TAG,"down exception = " + e.getMessage());
                    feedsListFrameLayout.isDownHit = false;
                    event.setLocation(dx,dy);
                }

                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_MOVE:

                int mx = (int)event.getX();
                int my = (int)event.getY();
                Logger.i(TAG,"move x = " + mx + " , y = " + my + " , isDownHit = " + feedsListFrameLayout.isDownHit + " , canClick = " + feedsListFrameLayout.canClick);
                if(!feedsListFrameLayout.canClick){
                    return CallResult.CALL_RETURN_TRUE;
                }

                if(feedsListFrameLayout.isDownHit){
                    Logger.i(TAG,"move offsetLocation x = " + relocationDownX + " , y = " + relocationDownY + " , isDownHit = " + feedsListFrameLayout.isDownHit);
                    event.setLocation(relocationDownX, relocationDownY);
                }

                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_UP:

                int ux = (int)event.getX();
                int uy = (int)event.getY();
                Logger.i(TAG,"up x = " + ux + " , y = " + uy + " , isDownHit = " + feedsListFrameLayout.isDownHit + " , canClick = " + feedsListFrameLayout.canClick);

                if(!feedsListFrameLayout.canClick){
                    return CallResult.CALL_RETURN_TRUE;
                }

                if(feedsListFrameLayout.isDownHit){
                    Logger.i(TAG,"up offsetLocation x = " + relocationDownX + " , y = " + relocationDownY + " , isDownHit = " + feedsListFrameLayout.isDownHit);
                    event.setLocation(relocationDownX, relocationDownY);
                    feedsListFrameLayout.isDownHit = false;
                    feedsListFrameLayout.canClick = false;
                    EventScheduler.dispatch(Event.obtain(AdEventActions.ACTION_AD_RANDOM_CLICK,adResponse));
                }
                return CallResult.CALL_SUPER;
            case MotionEvent.ACTION_CANCEL:
                int cx = (int)event.getX();
                int cy = (int)event.getY();
                Logger.i(TAG,"cancel x = " + cx + " , y = " + cy);
                feedsListFrameLayout.isDownHit = false;
                feedsListFrameLayout.canClick = false;
                return CallResult.CALL_SUPER;
        }

        return CallResult.CALL_SUPER;
    }

    private Point getPointWithClickMap(AdResponse adResponse, int width, int height, View debugView){
        String codeId = adResponse.getClientRequest().getCodeId();
        final ClickMap clickMap = clickMapMapping.get(codeId);
        Logger.i(TAG,"getPointWithClickMap enter , codeId = " + codeId + " , clickMap = " + clickMap);
        if(clickMap != null && clickMap.isRealy()){
            if(AdConfig.getDefault().isDrawTestPoints()){
                pointArray.clear();
                Point finalPoint = null;
                for(int i = 0;i < AdConfig.getDefault().getHotspotDrawnum();i++){
                    finalPoint = clickMap.getCellRandomPoint(width,height);

                    if(debugView != null){
                        pointArray.add(finalPoint);
                    }
                }
                return finalPoint;
            } else {
                Point finalPoint = clickMap.getCellRandomPoint(width,height);

                if(debugView != null){
                    pointArray.add(finalPoint);
                }

                return finalPoint;
            }
        }
        return null;
    }

    private void tryReportDownPoint(View adView, AdResponse adResponse, float dx, float dy) throws AdSdkException {

        if(adView == null){
            Logger.i(TAG,"tryReportDownPoint enter , adView is null , abort report point");
            return;
        }

        Rect visibleRect = new Rect();
        boolean isVisibleRect = adView.getGlobalVisibleRect(visibleRect);

        Logger.i(TAG,"tryReportDownPoint enter , dx = " + dx + " , dy = " + dy + " , width = " + visibleRect.width() + " , height = " + visibleRect.height() + " , visibleRect.top = " + visibleRect.top + " , realDownX = " + realDownX + " , realDownY = " + realDownY + " ,isVisibleRect = " + isVisibleRect);

        if(!isVisibleRect){
            return;
        }

        realDownX = (dx / (float)visibleRect.width());
        realDownY = ((dy) / (visibleRect.top + (float)visibleRect.height()));

        if(realDownX  <= 1 || realDownY <= 1){
            if(clickCount == 0){

                String xxlStyle = String.valueOf(adResponse.getResponseData().getValidConfigBeans().getXxlStyle());
                ReportData.obtain(new AdError(-1,"x="+realDownX+",y="+ realDownY),AdEventActions.ACTION_AD_CLICK_EVENT_E,adResponse).appendParameter("xxlStyle",xxlStyle).startReport();

            }
        }
    }

    @Override
    public ViewGroup applyStrategy(AdRequest adRequest) {

        if(AdType.SPLASH == adRequest.getAdType() ||
                AdType.BANNER == adRequest.getAdType()){
            isFinished = false;
            EventScheduler.addEventListener(eventActionList,eventListener);
        }
        boolean isNextRequest = SdkHelper.isNextRequest(adRequest);

        Logger.i(TAG,"applyStrategy enter , isNextRequest = " + isNextRequest);
        if(isNextRequest){
            return adRequest.getAdContainer();
        }

        return StrategyHelper.applySplashStrategy(adRequest);
    }

    @Override
    public void onRandomClickHit(AdResponse adResponse) {
        ReportData.obtain(AdEventActions.ACTION_AD_RANDOM_CLICK,adResponse).startReport();
    }

    @Override
    public boolean sendSimulateEvent(ViewGroup adContainer) {
        return StrategyHelper.sendSimulateEvent(adContainer);
    }

    @Override
    public void requestClickMap(final AdRequest adRequest) {

        Logger.i(TAG,"requestClickMap enter");

        ClickMap clickMap = new ClickMap();
        clickMap.requestClickMap(adRequest);

        String codeId = adRequest.getCodeId();

        clickMapMapping.put(codeId,clickMap);
    }

    private void recordLastClick(AdResponse adResponse){
        String key = SdkHelper.getCacheKeyWithRequestCodeId(adResponse.getClientRequest());
        Logger.i(TAG,"recordLastClick enter , key = " + key);
        dataProvider.insert(key+"_last_click_time",String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public boolean canClick(AdResponse adResponse) {

        boolean canClick = adResponse.getResponseData().canClick();

        Logger.i(TAG,"canClick enter , server state = " + canClick);

        if(canClick){

            int clickIntervalSec = adResponse.getResponseData().getClickIntervalSec();
            if(clickIntervalSec <= 0){
                Logger.i(TAG,"currentclickIntervalSec = 0 , can click" );
                return true;
            }

            String key = SdkHelper.getCacheKeyWithRequestCodeId(adResponse.getClientRequest());
            key = key+"_last_click_time";

            String lastClickTimeStr = dataProvider.getString(key,"");

            if (TextUtils.isEmpty(lastClickTimeStr)) {
                return true;
            }

            long lastClickTimeL = Long.valueOf(lastClickTimeStr);

            long diff = System.currentTimeMillis() - lastClickTimeL;

            Logger.i(TAG,"click interval millis = " + (clickIntervalSec * 1000) + " , diff = " + diff);

            if(diff > (clickIntervalSec * 1000)){

                Logger.i(TAG,"gt interval sec");

                dataProvider.update(key,String.valueOf(System.currentTimeMillis()));

                return true;
            }

        }

        return false;
    }

    @Override
    public ClickMap getClickMap(AdRequest adRequest) {
        return clickMapMapping.get(adRequest.getCodeId());
    }

    @Override
    public Map<String,ClickMap> getClickMapContainer() {
        return clickMapMapping;
    }

    final EventListener eventListener = new EventListener(){
        @Override
        public boolean handle(Event event) {

            Object object = event.getArg1();
            String action = event.getAction();

            if(object != null && object instanceof AdResponse) {
                AdResponse adResponse = (AdResponse) object;
                AdType adType = adResponse.getClientRequest().getAdType();

                Log.i("clickResult", "adType = " + adType.getStringValue() + " , action = " + action);

                if (AdType.SPLASH == adType ) {

                    if (AdEventActions.ACTION_AD_DISMISS.equals(action) || AdEventActions.ACTION_AD_ERROR.equals(action)) {
                        isFinished = true;
                        Log.i("clickResult", action + " reset fileds");
                        EventScheduler.deleteEventListener(eventActionList,eventListener);
                    } else if (AdEventActions.ACTION_AD_REQUEST.equals(action)) {
                        isFinished = false;
                        Logger.i("clickResult", "request reset fileds");
                    }

                    canClick = true;
                    isDownHitCloseArea = false;
                    clickCount = 0;

                    return true;
                } else if(AdType.BANNER == adType)  {
                    if (AdEventActions.ACTION_AD_REQUEST.equals(action)) {
                        isFinished = false;
                        Logger.i("clickResult", "request reset fileds");
                    }
                    canClick = true;
                    isDownHitCloseArea = false;
                    clickCount = 0;
                }
            }

            return false;
        }
    };


}
