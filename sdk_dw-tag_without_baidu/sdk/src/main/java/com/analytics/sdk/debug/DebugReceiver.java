package com.analytics.sdk.debug;

import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.Singleton;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.ClickMap;
import com.analytics.sdk.service.ad.IAdStrategyService;
import com.analytics.sdk.view.activity.FloatWindowActivity;
import com.analytics.sdk.view.strategy.os.AndroidHackHelper;
import com.analytics.sdk.view.widget.floatwin.FloatWindowManager;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SDK 进入debug模式
 * 由adb命令来发送广播
 *
 * 开启 debug :  adb shell am broadcast -a sdk.LOG
 * 切换环境：    adb shell am broadcast -a sdk.CHANGE_ENV
 *
 * @author devy
 */
public class DebugReceiver extends BroadcastReceiver {

    public static final String ACTION_LOG = "sdk.LOG";
    public static final String ACTION_CHANGE_ENV = "sdk.CHANGE_ENV";
    public static final String ACTION_PRINT_CONFIG = "sdk.PRINT_CONFIG";
    public static final String ACTION_CLEAR_CACHE = "sdk.CLEAR_CACHE";
    public static final String ACTION_LOG2FILE = "sdk.LOG2FILE";
    public static final String ACTION_CLICK_STRATEGY = "sdk.CLICK_STRATEGY";
    public static final String ACTION_AUTOMATOR = "sdk.AUTOMATOR";
    public static final String ACTION_HACK = "sdk.HACK";
    public static final String ACTION_PROGUARD = "sdk.PROGUARD";
    public static final String ACTION_DRAW_CLICK_MAP = "sdk.CLICK_MAP";
    public static final String ACTION_DRAW_CLICK_MAP_TEST_POINTS = "sdk.CLICK_MAP_TEST_POINTS";
    public static final String ACTION_PRINT_CLICK_MAP = "sdk.CLICK_MAP_PRINT";
    public static final String ACTION_PRINT_DYNAMIC = "sdk.DYNAMIC_PRINT";
    public static final String ACTION_PRINT_CLICK_MAP_CELL_VALUE = "sdk.CLICK_MAP_PRINT_CELL_VALUE";
    public static final String ACTION_PRINT_CACHE = "sdk.PRINT_CACHE";
    public static final String ACTION_EXECUTE_DEX = "sdk.EXECUTE_DEX";
    public static final String ACTION_OPEN_DEBUG_PLUING_PATH = "sdk.OPEN_DEBUG_PLUGIN_PATH";
    public static final String ACTION_OPEN_DEBUG_FLOAT_VIEW = "sdk.OPEN_FLOAT_VIEW";

    static boolean isAutomatorEnv = false;

    public static boolean isAutomatorEnv(){
        return isAutomatorEnv;
    }

    private static DebugReceiver debugReceiver = null;

    public static void startReceiver(Context context){
        if(debugReceiver == null){
            Logger.i("DebugReceiver","startReceiver enter");
            debugReceiver = new DebugReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_LOG);
            intentFilter.addAction(ACTION_CHANGE_ENV);
            intentFilter.addAction(ACTION_PRINT_CONFIG);
            intentFilter.addAction(ACTION_LOG2FILE);
            intentFilter.addAction(ACTION_CLEAR_CACHE);
            intentFilter.addAction(ACTION_CLICK_STRATEGY);
            intentFilter.addAction(ACTION_AUTOMATOR);
            intentFilter.addAction(ACTION_HACK);
            intentFilter.addAction(ACTION_PROGUARD);
            intentFilter.addAction(ACTION_DRAW_CLICK_MAP);
            intentFilter.addAction(ACTION_DRAW_CLICK_MAP_TEST_POINTS);
            intentFilter.addAction(ACTION_PRINT_CLICK_MAP);
            intentFilter.addAction(ACTION_PRINT_DYNAMIC);
            intentFilter.addAction(ACTION_PRINT_CLICK_MAP_CELL_VALUE);
            intentFilter.addAction(ACTION_PRINT_CACHE);
            intentFilter.addAction(ACTION_OPEN_DEBUG_PLUING_PATH);
            intentFilter.addAction(ACTION_EXECUTE_DEX);
            intentFilter.addAction(ACTION_OPEN_DEBUG_FLOAT_VIEW);

            context.registerReceiver(debugReceiver,intentFilter);

            try {
                String isEnable = CacheHelper.getHelper().getAsString("log_enable_time");
                if(!TextUtils.isEmpty(isEnable)){
                    AdConfig.getDefault().setPrintLog(true);
                    Logger.forcePrint("DebugReceiver","setPrintLog true from cache");
                }

                String isEnableLog2File = CacheHelper.getHelper().getAsString("log2file_enable_time");
                if(!TextUtils.isEmpty(isEnableLog2File)){
                    AdConfig.getDefault().setWriteLog2File(true);
                    Logger.forcePrint("DebugReceiver","setWriteLog2File true from cache");
                }

                String isEnableDebugStrategy = CacheHelper.getHelper().getAsString("log_enable_click_strategy");
                if(!TextUtils.isEmpty(isEnableDebugStrategy)){
                    AdConfig.getDefault().setDebugClickStrategy(true);
                    Logger.forcePrint("DebugReceiver","setDebugClickStrategy true from cache");
                }

            } catch (Throwable t){
                t.printStackTrace();
            }

        }
    }

    public static boolean isStarted(){
        return (debugReceiver != null);
    }

    public static void stopReceiver(Context context){
        if(isStarted()){
            context.unregisterReceiver(debugReceiver);
            debugReceiver = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DebugReceiver","onReceive enter , action = " + (intent!=null ? intent.getAction() : "empty"));

        if(intent == null){
            return;
        }

        String action = intent.getAction();
        AdConfig config = AdConfig.getDefault();

        try {

            if(ACTION_OPEN_DEBUG_FLOAT_VIEW.equals(action)){

                if(!FloatWindowManager.getInstance().isShown()){
                    FloatWindowActivity.showFloatWindow(context);
                }

            } if (ACTION_OPEN_DEBUG_PLUING_PATH.equals(action)) {

                boolean result = !config.isDebugPluginPath();
                config.setDebugPluginPath(result);

            } else if(ACTION_EXECUTE_DEX.equals(action)){



            } else if(ACTION_PRINT_CACHE.equals(action)){

                StringBuilder cacheInfo = new StringBuilder();

                File cacheDir = CacheHelper.getHelper().getCacheDir();

                if(cacheDir == null || (cacheInfo != null && !cacheDir.exists())){
                    cacheInfo.append("cacheDir = null");
                } else {
                    cacheInfo.append("cacheDir = ").append(cacheDir.getAbsolutePath()).append("\n");

                    List<String> keys = CacheHelper.getHelper().listKeys();

                    if(keys != null && keys.size() > 0){
                        cacheInfo.append("cache key size = " + keys.size()).append("\n");
                        for(int i = 0;i < keys.size();i++) {
                            String f = keys.get(i);
                            cacheInfo.append("cache item = " + f).append("\n");
                        }
                    } else {
                        cacheInfo.append("cache file size 0");
                    }

                }

                setResultData(" \n\n"+cacheInfo.toString()+"\n\n");

                return;

            } else if(ACTION_PRINT_CLICK_MAP_CELL_VALUE.equals(action)) {
                boolean result = !config.isDrawCellValue();
                config.setDrawCellValue(result);
            } else if(ACTION_PRINT_DYNAMIC.equals(action)) {


                return;

            } else if(ACTION_PRINT_CLICK_MAP.equals(action)) {

                IAdStrategyService adStrategyService = ServiceManager.getService(IAdStrategyService.class);
                Map<String,ClickMap> map = adStrategyService.getClickMapContainer();

                StringBuilder clickMapInfos = new StringBuilder();

                for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext();){
                    String codeId = iter.next();
                    ClickMap clickMap = map.get(codeId);
                    clickMapInfos.append("codeId = ").append(codeId).append(" , ").append("clickMap = ").append(clickMap).append("\n");
                }

                setResultData(" \n\n"+clickMapInfos.toString()+"\n\n");

                return;

            } else if(ACTION_DRAW_CLICK_MAP.equals(action)) {

                boolean result = !config.isDrawCells();
                config.setDrawCells(result);

            } else if(ACTION_DRAW_CLICK_MAP_TEST_POINTS.equals(action)) {

                boolean result = !config.isDrawTestPoints();
                Integer numder = Integer.valueOf(intent.getStringExtra("numder"));
                config.setHotspotDrawnum(numder);
                config.setDrawTestPoints(result);

            } else if(ACTION_LOG.equals(action)){
                boolean result = !config.isPrintLog();
                config.setPrintLog(result);
                if(result){
                    CacheHelper.getHelper().put("log_enable_time",String.valueOf(result),3 * 60 * 60);
                } else {
                    CacheHelper.getHelper().remove("log_enable_time");
                }
            } else if(ACTION_LOG2FILE.equals(action)) {

                boolean result = !config.isWriteLog2File();
                config.setWriteLog2File(result);

                if(result){
                    CacheHelper.getHelper().put("log2file_enable_time",String.valueOf(result),3 * 60 * 60);
                } else {
                    CacheHelper.getHelper().remove("log2file_enable_time");
                }

            } else if(ACTION_CLICK_STRATEGY.equals(action)){
                boolean result = !config.isDebugClickStrategy();
                config.setDebugClickStrategy(result);
                if(result){
                    CacheHelper.getHelper().put("log_enable_click_strategy",String.valueOf(result),3 * 60 * 60);
                } else {
                    CacheHelper.getHelper().remove("log_enable_click_strategy");
                }
            } else if(ACTION_CHANGE_ENV.equals(action)) {
//                config.getServerEnvConfig().setReleaseEnv(!(config.getServerEnvConfig().isReleaseEnv()));
                config.getServerEnvConfig().setSdkServerEnv(Integer.valueOf(intent.getStringExtra("env")));
            } else if(ACTION_PRINT_CONFIG.equals(action)) {
                String configInfo = config.toString();
                setResultData(" \n\n "+configInfo+" \n\n");
                return;
            } else if(ACTION_CLEAR_CACHE.equals(action)) {
                CacheHelper.getHelper().clear();
            } else if(ACTION_AUTOMATOR.equals(action)) {
                isAutomatorEnv = !(isAutomatorEnv);
            } else if(ACTION_PROGUARD.equals(action)) {

                String clientService = "com.analytics.sdk.service.client.IClientServcie";
                String adService = "com.analytics.sdk.service.ad.IAdService";

                try {
                    Class.forName(clientService);
                    Class.forName(adService);
                    setResultData(" \n\n ** proguard normal state ** \n\n");
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    setResultData(" \n\n ** "+e.getMessage()+" ** \n\n");
                    return;
                }

            } else if(ACTION_HACK.equals(action)) {

                try {
                    Object activityThreadObj = AndroidHackHelper.getsActivityThread2(AdClientContext.getClientContext());
                    Instrumentation baseInstrumentation = AndroidHackHelper.getInstrumentation2(activityThreadObj);

                    Singleton<IActivityManager> defaultSingleton = AndroidHackHelper.getIActivityManager();
                    IActivityManager origin = defaultSingleton.get();

                    StringBuilder osInfo = new StringBuilder();

                    osInfo.append("ActivityThread = ").append(activityThreadObj.getClass().getName()).append("\n")
                            .append("Instrumentation = ").append(baseInstrumentation.getClass().getName()).append("\n")
                            .append("IActivityManager = ").append(origin.getClass().getName()).append("\n");

                    setResultData(" \n\n"+osInfo.toString()+"\n\n");

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            setResultData(" \n\n ** action("+action+") operate success ** \n\n");

        } catch (Exception e) {
            e.printStackTrace();

            setResultData(" \n\n ** action("+action+") operate error("+e.getMessage()+") ** \n\n");
        }
    }


}
