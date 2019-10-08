package com.analytics.sdk.service.ad;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.helper.AES;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DateHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.http.toolbox.JsonObjectPostRequest;
import com.analytics.sdk.common.http.toolbox.StringRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.config.CodeIdConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.ad.entity.AdRequestParameters;
import com.analytics.sdk.service.ad.entity.BWPackageList;
import com.analytics.sdk.service.common.RequestParameterBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ISpamServiceImpl extends AbstractService implements ISpamService {

    static final String TAG = ISpamServiceImpl.class.getSimpleName();

    private List<String> safePackageList = new ArrayList<>();
    private List<String> allUserApps = new ArrayList<>();
    private DataProvider spamDataProvider = null;

    static final String KEY_BW_PACKAGES = "getBWPackages";

    public ISpamServiceImpl() {
        super(ISpamService.class);
        //微信
        safePackageList.add("com.tencent.mm");
        safePackageList.add("com.tencent.mobileqq");
    }

    @Override
    public void init(final Context context) {
        super.init(context);

        spamDataProvider = DataProvider.newProvider(context,"spam_data_source").startLoad();

        if(isSupportSpam()){
            Logger.i(TAG,"init start");
            ThreadExecutor.runOnCachedThreadPool(new Runnable() {
                @Override
                public void run() {

                    DeviceHelper.startCollectBattery(context);

                    allUserApps.clear();
                    allUserApps.addAll(DeviceHelper.getAllUserApps());

                    try {
                        getBWPackages();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    reportInstalledPackages();

                }
            });
        } else {
            Logger.i(TAG,"init abort");
        }

    }

    @Override
    public boolean isRootedDevice() {
        return DeviceHelper.isRootedDevice();
    }

    @Override
    public boolean isLowDevice() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public boolean isSafePackage() {
        Context context = AdClientContext.getClientContext();

        int size = safePackageList.size();

        for(int i = 0;i < size;i++){
            String packageName = safePackageList.get(i);

            if(AppHelper.exist(context,packageName)){
                return true;
            }

        }

        return false;
    }

    public String getBWPackageListFromCache(){
        return CacheHelper.getHelper().getAsString(KEY_BW_PACKAGES);
    }

    public void saveCache(String data,long saveTime){
        CacheHelper.getHelper().put(KEY_BW_PACKAGES,data);
    }

    @Override
    public boolean getBWPackages() throws JSONException {

        Logger.i(TAG,"getBWPackages enter");

        String cacheData = getBWPackageListFromCache();

        if(!TextUtils.isEmpty(cacheData)) {
            try {
                BWPackageList cacheBWPackageList = BWPackageList.parse(cacheData);
                AdConfig.getDefault().setBwPackageList(cacheBWPackageList);
                Logger.i(TAG,"getBWPackages cache hit it");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        final String requestUrl = AdConfig.getDefault().getServerEnvConfig().getWBPackageListUrl();
        JSONObject requestParams = RequestParameterBuilder.buildJsonObjectParameters();

        Logger.printJson(SdkHelper.format(requestParams.toString()),"getBWPackages requestUlr = "+requestUrl+" , params ↓");

        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, requestParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if(TextUtils.isEmpty(response)){
                        Logger.i(TAG,"*getBWPackages.onResponse empty*");
                        return;
                    }
                    String decodeResult = AES.decode(response);

                    BWPackageList serverBWPackageList = BWPackageList.parse(decodeResult);

                    Logger.printJson(SdkHelper.format(decodeResult),"getBWPackages from server("+requestUrl+") ↓");

                    int intervalDays = serverBWPackageList.getIntervalDays();

                    if(intervalDays >= 0){
                        saveCache(decodeResult,intervalDays * 24 * 60 * 60);
                    }

                    AdConfig.getDefault().setBwPackageList(serverBWPackageList);

                } catch (Exception e) {
                    e.printStackTrace();
                    log(IAdServiceImpl.class,"getBWPackages.onResponse handle exception " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log(IAdServiceImpl.class,"getBWPackages.onErrorResponse enter, error = " + error.getMessage());
            }
        });

        HttpHelper.send(jsonObjectPostRequest);

        return true;
    }

    @Override
    public boolean reportInstalledPackages() {

        final String cacheKey = "reportInstalledPackages";

        try {
            String hitIt = CacheHelper.getHelper().getAsString(cacheKey);
            if(!TextUtils.isEmpty(hitIt)) {
                Logger.i(TAG,"* abort reportInstalledPackages , reason: time is not up*");
                return false;
            }
            CacheHelper.getHelper().put(cacheKey,"installed",2 * 24 * 60 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AdRequestParameters requestInfo = AdRequestParameters.buildRequest(AdClientContext.getClientContext(),null);
        JSONObject requestParams = requestInfo.toJson();

        try {
            requestParams.put("installedPkgs", DeviceHelper.getAllUseApps(AdClientContext.getClientContext()));
            requestParams.put("device_id", DeviceHelper.getImei(AdClientContext.getClientContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requestUrl = AdConfig.getDefault().getServerEnvConfig().getDeviceReportUrl();
        Logger.printJson(SdkHelper.format(requestParams.toString()),"ISpamServiceImpl#reportInstalledPackages requestUlr = "+requestUrl+" , params ↓");

        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, requestParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Logger.i(TAG,"*reportInstalledPackages.onResponse success*");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log(IAdServiceImpl.class,"reportInstalledPackages.onErrorResponse enter");
            }
        });

        HttpHelper.send(jsonObjectPostRequest);

        return true;
    }

    @Override
    public boolean reportExcpIMEI(int code) {
        Logger.i(TAG,"reportExcpIMEI enter , code = " + code);
        Context context = AdClientContext.getClientContext();

        AdRequestParameters adRequestParameters = AdRequestParameters.buildRequest(context,null);
        JSONObject jsonObject = adRequestParameters.toJson();
        try {
            jsonObject.put("code",code);
            jsonObject.put("device_id", DeviceHelper.getImei(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requestUrl = AdConfig.getDefault().getServerEnvConfig().getExcpIMEIReportUrl();

        Logger.printJson(SdkHelper.format(jsonObject.toString()),"reportExcpIMEI requestUlr = "+requestUrl+" , params ↓");

        String requestEncodeParamsResult = AES.encode(jsonObject.toString());

        StringRequest jsonObjectPostRequest = new StringRequest(requestUrl, requestEncodeParamsResult, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.i(TAG,"*reportExcpIMEI.onResponse success*");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log(IAdServiceImpl.class,"reportExcpIMEI.onErrorResponse enter, error = " + error.getMessage());
            }
        });

        HttpHelper.send(jsonObjectPostRequest);

        return true;
    }

    @Override
    public boolean isHitBlack() {

        //是否支持反作弊策略
        boolean isSupportSpam = AdConfig.getDefault().getServerInitConfig().isSupportSpam();

        if(!isSupportSpam){
            Logger.i(TAG,"isHitBlack enter,not support spam");
            return false;
        }

        //服务器认为当前设备为作弊设备
        boolean isBlackSpamState = isBlackStateFromServer();

        //服务器认为当前设备为作弊设备
        if(isBlackSpamState){
            Logger.i(TAG,"isHitBlack enter,hit server state");
            return true;
        }

        BWPackageList bwPackageList = AdConfig.getDefault().getBwPackageList();
        if(bwPackageList != null){
            //当前存在黑名单包名

            if(allUserApps.size() > 0){

                List<String> bList = bwPackageList.getbList();

                for(String appName : allUserApps){
                    if(bList.contains(appName)){
                        Logger.i(TAG,"isHitBlack enter,hit black package list , appName = " + appName);
                        return true;
                    }
                }
            }

        }

        return false;
    }

    @Override
    public boolean isHitGray() {
        //是否支持反作弊策略
        boolean isSupportSpam = AdConfig.getDefault().getServerInitConfig().isSupportSpam();

        if(!isSupportSpam){
            Logger.i(TAG,"isHitGray enter,not support spam");
            return false;
        }

        boolean isGraySpamState = isGrayStateFromServer();

        if(isGraySpamState){
            Logger.i(TAG,"isHitGray enter,hit server state");
            return true;
        }

        //后面的逻辑先不添加
        if(true){
            return false;
        }

        //是否存在qq或者微信等
        boolean existSafePackage = isSafePackage();

        //是否是root设置
        boolean isRootDevice = isRootedDevice();
        //4.4以下设备
        boolean isLowDevice = isLowDevice();

        Logger.i(TAG,"isHitGray enter , existSafePackage = " + existSafePackage + " , isRootDevice = " + isRootDevice + " , isLowDevice = " + isLowDevice + " , isGraySpamState = " + isGraySpamState + " , userAppSize = " + allUserApps.size());

        if(isRootDevice || isLowDevice) {

            BWPackageList bwPackageList = AdConfig.getDefault().getBwPackageList();
            //当前存在白名单包名
            boolean containsWPackageList = false;

            if(bwPackageList != null && bwPackageList.getbList().size() > 0){
                //当前存在黑名单包名

                if(allUserApps.size() > 0){

                    List<String> bList = bwPackageList.getbList();
                    for(String appName : allUserApps){
                        if(bList.contains(appName)){
                            Logger.i(TAG,"isHitGray enter,hit white package list , appName = " + appName);
                            containsWPackageList = true;
                            break;
                        }
                    }
                }

                if(!containsWPackageList){
                    Logger.i(TAG,"hit other#1");
                    return true;
                }

            } else {
                if(!existSafePackage){
                    Logger.i(TAG,"hit other#2");
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean increateExposureCount(String codeId){

        //是否支持反作弊策略
        boolean isSupportSpam = AdConfig.getDefault().getServerInitConfig().isSupportSpam();

        if(!isSupportSpam){
            Logger.i(TAG,"increateExposureCount enter,not support spam");
            return false;
        }

        IAdService adService = ServiceManager.getService(IAdService.class);
        CodeIdConfig adServerConfig = adService.getCodeIdConfig(codeId);

        if(adServerConfig == null){
            return true;
        }

        if(adServerConfig.getDayExposureCount() > 0){
            String dayKey = "day_"+DateHelper.currentDate();
            int currentDateCount = spamDataProvider.getInt(dayKey,0);
            spamDataProvider.insertInt(dayKey,++currentDateCount);
            Logger.i(TAG,"increateExposureCount enter , currentDateCount = " + currentDateCount);
        }

        if(adServerConfig.getHourExposureCount() > 0){
            String hourKey = "hour_"+DateHelper.currentDateHour();
            int currentDateHourCount = spamDataProvider.getInt(hourKey,0);
            spamDataProvider.insertInt(hourKey,++currentDateHourCount);
            Logger.i(TAG,"increateExposureCount enter , currentDateHourCount = " + currentDateHourCount);
        }

        return true;
    }

    @Override
    public boolean isGtExposureMaxCount(String codeId) {
        //是否支持反作弊策略
        boolean isSupportSpam = AdConfig.getDefault().getServerInitConfig().isSupportSpam();

        if(!isSupportSpam){
            Logger.i(TAG,"isGtExposureMaxCount enter,not support spam");
            return false;
        }

        boolean result = isGtExposureMaxCountInner(codeId);
        tryClearPreData();
        return result;
    }

    boolean isGtExposureMaxCountInner(String codeId) {

        IAdService adService = ServiceManager.getService(IAdService.class);
        CodeIdConfig adServerConfig = adService.getCodeIdConfig(codeId);

        if(adServerConfig == null){
            return false;
        }

        int dayMaxCount = adServerConfig.getDayExposureCount();

        if(dayMaxCount > 0){

            String dayKey = "day_"+DateHelper.currentDate();
            int currentDateCount = spamDataProvider.getInt(dayKey,0);

            Logger.i(TAG,"isGtExposureMaxCount enter , dayMaxCount = " + dayMaxCount + " , currentDateCount = " + currentDateCount + " , dayKey = " + dayKey);

            if(currentDateCount >= dayMaxCount) {
                Logger.i(TAG,"hit gt day-count");
                return true;
            }
        }

        int hourMaxCount = adServerConfig.getHourExposureCount();

        if(hourMaxCount > 0){
            String hourKey = "hour_"+DateHelper.currentDateHour();
            int currentDateHourCount = spamDataProvider.getInt(hourKey,0);

            Logger.i(TAG,"isGtExposureMaxCount enter , hourMaxCount = " + hourMaxCount +" , currentDateHourCount = " + currentDateHourCount + " , hourKey = " + hourKey);

            if(currentDateHourCount >= hourMaxCount) {
                Logger.i(TAG,"hit gt hour-count");
                return true;
            }
        }

        return false;
    }

    private void tryClearPreData(){
        ThreadExecutor.runOnCachedThreadPool(new Runnable() {
            @Override
            public void run() {
                String preDate = DateHelper.addCurrentDate(-1);
                String dateKey = "day_"+preDate;
                String hourKey = "hour_"+preDate;

                Logger.i(TAG,"tryClearPreData enter , preDate = " + preDate);

                if(spamDataProvider.has(dateKey)){
                    spamDataProvider.delete(dateKey);
                    Logger.i(TAG,"delete dateKey");
                }
                if(spamDataProvider.has(hourKey)){
                    spamDataProvider.delete(hourKey);
                    Logger.i(TAG,"delete hourKey");
                }

                Logger.i(TAG,"spamDataProvider size = " + spamDataProvider.size());

            }
        });
    }

    @Override
    public boolean isSupportSpam() {
        return AdConfig.getDefault().getServerInitConfig().isSupportSpam();
    }

    @Override
    public boolean canClick(AdType adType) {

        //是否支持反作弊策略
        boolean isSupportSpam = AdConfig.getDefault().getServerInitConfig().isSupportSpam();

        if(!isSupportSpam){
            Logger.i(TAG,"canClick enter,not support spam");
            return true;
        }

        boolean canClickResult = true;

        if(AdType.SPLASH  == adType){
            canClickResult = (AdConfig.getDefault().getServerInitConfig().getSplashCanClick() == 0);
        } else if(AdType.INFORMATION_FLOW == adType) {
            canClickResult = (AdConfig.getDefault().getServerInitConfig().getFeedlistCanClick() == 0);
        }

        Logger.i(TAG,"canClick enter , result("+adType+") = " + canClickResult);

        return canClickResult;
    }

    @Override
    public boolean isGrayStateFromServer() {
        return AdConfig.getDefault().getServerInitConfig().getDeviceSpamState() == ISpamService.SpamState.STATE_GARY;
    }

    @Override
    public boolean isBlackStateFromServer() {
        return AdConfig.getDefault().getServerInitConfig().getDeviceSpamState() == ISpamService.SpamState.STATE_BLACK;
    }

}
