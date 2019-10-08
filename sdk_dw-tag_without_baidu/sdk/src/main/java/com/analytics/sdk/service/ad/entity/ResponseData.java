package com.analytics.sdk.service.ad.entity;

import android.text.TextUtils;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.service.DataSource;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ad.IAdService;
import com.analytics.sdk.service.ad.StrategyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseData {

    /**
     *
     * 标识没有数据,避免传递null对象
     * 1. 当服务器错误
     * 2. 没有在缓存中找到数据时
     * 3. 其他标识无返回的场景
     */
    public static final ResponseData NO_RESPONSE = new ResponseData();

    /**
     * 误点率
     */
    private float cr;
    private String errorCode;
    private boolean log;
    private String msg;
    private int priority;
    private String requestId;
    private int sec;
    private int slotType;
    private int source = DataSource.UNKNOW;
    private int type;
    private int orientation;
    private List<ConfigBeans> params = new ArrayList<>();
    private List<YdtAdBean> ads = new ArrayList<>();
    private List<AdShowStrategy> strategyList = new ArrayList<>();

    /**
     * 缓存有效时间,默认为3小时,3小时内使用缓存不请求服务器
     */
    private int cacheValidTime = 3 * 60 * 60;
    /**
     * 是否使用缓存
     */
    private boolean isUseCache = false;

    /**
     * 点击间隔时间
     */
    private int clickIntervalSec = 0;
    /**
     *  1:可以点击
     *  0: 不允许点击
     */
    private int canClick = 1;

    public boolean canClick(){
        return canClick == 1;
    }

    public int getClickIntervalSec() {
        return clickIntervalSec;
    }

    public void setClickIntervalSec(int clickIntervalSec) {
        this.clickIntervalSec = clickIntervalSec;
    }

    public int getCanClick() {
        return canClick;
    }

    public void setCanClick(int canClick) {
        this.canClick = canClick;
    }

    public boolean isHitErrorApiAd(){
        List<AdShowStrategy> strategyList = getStrategyList();
        if(strategyList == null){
            return false;
        }
        if(strategyList.size() > 0){
            AdShowStrategy adShowStrategy = strategyList.get(0);
            if(adShowStrategy == null){
                return false;
            }
            // e 即表示error
            if (StrategyHelper.isHit(adShowStrategy.getProbability()) && adShowStrategy.getAction().equals("e")){
                return true;
            }
        }
        return false;
    }

    public boolean isHitClickApiAd(){
        List<AdShowStrategy> strategyList = getStrategyList();
        if(strategyList == null){
            return false;
        }
        if(strategyList.size() > 0){
            AdShowStrategy adShowStrategy = strategyList.get(0);
            if(adShowStrategy == null){
                return false;
            }
            // e 即表示error
            if (StrategyHelper.isHit(adShowStrategy.getProbability()) && adShowStrategy.getAction().equals("c")){
                return true;
            }
        }
        return false;
    }

    public static ResponseData obtainDefault(AdRequest adRequest){
        if(AdConfig.getDefault().getAd3rdSdkConfig().isSupport3rdSdkDefaultConfig()) {

            AdType adType = adRequest.getAdType();

            if(AdType.SPLASH == adType){

                int adSource = AdConfig.getDefault().getAd3rdSdkConfig().getSplashDefaultAdSource();
                String appId = AdConfig.getDefault().getAd3rdSdkConfig().getSplashDefualtAppId();
                String codeId = AdConfig.getDefault().getAd3rdSdkConfig().getSplashDefaultSloatId();
                String pkg = AdConfig.getDefault().getAd3rdSdkConfig().getSplashPackageName();

                if(TextUtils.isEmpty(appId) || TextUtils.isEmpty(codeId)) {
                    return ResponseData.NO_RESPONSE;
                }

                return obtainDefault(adSource,appId,codeId,AdFillType.TEMPLATE.intValue(),BuildConfig.DEFUALT_APP_NAME,pkg);

            }

        }
        return ResponseData.NO_RESPONSE;
    }

    public static ResponseData obtainWithBuildConfig(AdRequest adRequest, ConfigBeans configBeans){
        if(AdConfig.getDefault().getAd3rdSdkConfig().isSupport3rdSdkDefaultConfig()) {

            AdType adType = adRequest.getAdType();

            if(AdType.SPLASH == adType){

                int adSource = configBeans.getSource();
                String appId = configBeans.getAppId();
                String codeId = configBeans.getSlotId();
                String appName = configBeans.getAppName();
                String pkg = configBeans.getPkg();

                if(TextUtils.isEmpty(appId) || TextUtils.isEmpty(codeId)) {
                    return ResponseData.NO_RESPONSE;
                }

                return obtainDefault(adSource,appId,codeId,AdFillType.TEMPLATE.intValue(),appName,pkg);

            }

        }
        return ResponseData.NO_RESPONSE;
    }

    public static ResponseData forceObtain(AdRequest adRequest,ConfigBeans configBeans){

        int adSource = configBeans.getSource();
        int slotFill = configBeans.getSlotFill();
        String appId = configBeans.getAppId();
        String codeId = configBeans.getSlotId();
        String appName = configBeans.getAppName();
        String pkg = configBeans.getPkg();

        if(TextUtils.isEmpty(appId) || TextUtils.isEmpty(codeId)) {
            return ResponseData.NO_RESPONSE;
        }

        return obtainDefault(adSource,appId,codeId,slotFill,appName,pkg);
    }

    public static ResponseData obtainCSJDefault(AdRequest adRequest){
        if(AdConfig.getDefault().getAd3rdSdkConfig().isSupport3rdSdkDefaultConfig()) {

            AdType adType = adRequest.getAdType();

            if(AdType.SPLASH == adType){

                int adSource = BuildConfig.CSJ_SPLASH_DEFAULT_AD_SOURCE;
                String appId = BuildConfig.CSJ_SPLASH_DEFAULT_APP_ID;
                String codeId = BuildConfig.CSJ_SPLASH_DEFAULT_CODE_ID;

                if(TextUtils.isEmpty(appId) || TextUtils.isEmpty(codeId)) {
                    return ResponseData.NO_RESPONSE;
                }

                return obtainDefault(adSource,appId,codeId,AdFillType.TEMPLATE.intValue(),BuildConfig.DEFUALT_APP_NAME,null);

            }

        }
        return ResponseData.NO_RESPONSE;
    }

    static ResponseData obtainDefault(int adSource, String appId, String codeId,int slotFill,String appName,String pkg){
        ResponseData responseModel = new ResponseData();

        responseModel.setCr(BuildConfig.DEFUALT_RANDOM_CLICK_RATE);
        responseModel.setSource(adSource);
        responseModel.setLog(true);

        List<ConfigBeans> configBeansList = new ArrayList<>();

        ConfigBeans configBeans = new ConfigBeans();
        configBeans.setAppId(appId);
        configBeans.setSlotId(codeId);
        configBeans.setSlotFill(slotFill);
        configBeans.setSource(adSource);
        configBeans.setAppName(appName);
        configBeans.setPkg(pkg);

        configBeansList.add(configBeans);
        responseModel.setParams(configBeansList);
        return responseModel;
    }


    public boolean isNoReponse(){
        return (this == NO_RESPONSE);
    }

    public float getCr() {
        return cr;
    }

    public void setCr(float cr) {
        this.cr = cr;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getSlotType() {
        return slotType;
    }

    public void setSlotType(int slotType) {
        this.slotType = slotType;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public List<ConfigBeans> getParams() {
        return params;
    }

    public void setParams(List<ConfigBeans> params) {
        this.params = params;
    }

    public List<YdtAdBean> getAds() {
        return ads;
    }

    public List<AdShowStrategy> getStrategyList() {
        return strategyList;
    }

    public boolean hasStrategyWithWebView(){
        List<AdShowStrategy> configWeiList = getStrategyList();
        if (configWeiList != null && configWeiList.size() > 0) {
            AdShowStrategy adShowStrategy = configWeiList.get(0);
            if(adShowStrategy.getInteraction_type() == IAdService.JMP){
                return true;
            }
        }
        return true;
    }

    public void setStrategyList(List<AdShowStrategy> strategyList) {
        this.strategyList = strategyList;
    }

    public void setAds(List<YdtAdBean> ads) {
        this.ads = ads;
    }

    public int getCacheValidTime() {
        return cacheValidTime;
    }

    public void setCacheValidTime(int cacheValidTime) {
        this.cacheValidTime = cacheValidTime;
    }

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean useCache) {
        isUseCache = useCache;
    }

    public boolean isSdkSource(){
        if(getParams()!=null&&getParams().size()>0){
            return true;
        }
        return false;
    }

    public boolean isApiSource(){
        return (getValidMetaGroupList() != null || getStrategyList() != null);
    }

    public YdtAdBean.MetaGroupBean getValidFristMetaGroup() throws AdSdkException {
        List<YdtAdBean.MetaGroupBean> metaGroupBeanList = getValidMetaGroupList();
        if(metaGroupBeanList!=null){
            return metaGroupBeanList.get(0);
        }
        throw new AdSdkException("not found ydt ad data");
    }

    public YdtAdBean getValidFristYdtAdData() throws AdSdkException {
        List<YdtAdBean> ydtAdBeanList = getAds();

        if (ydtAdBeanList != null && ydtAdBeanList.size() > 0){

            YdtAdBean ydtAdBean = ydtAdBeanList.get(0);

            if(ydtAdBean != null){
                return ydtAdBean;
            }

        }
        throw new AdSdkException("not found ydt ad data");
    }


    public List<YdtAdBean.MetaGroupBean> getValidMetaGroupList(){
        List<YdtAdBean> ydtAdBeanList = getAds();

        if (ydtAdBeanList != null && ydtAdBeanList.size() > 0){

            YdtAdBean ydtAdBean = ydtAdBeanList.get(0);

            if(ydtAdBean != null){

                List<YdtAdBean.MetaGroupBean> list = ydtAdBean.getMetaGroup();

                if(list != null && list.size() > 0){
                    return list;
                }

            }

        }
        return null;
    }

    public ConfigBeans getValidConfigBeans() throws AdSdkException {
        if(isSdkSource()){
            return getParams().get(0);
        }
        throw new AdSdkException(ErrorCode.ApiServer.ERROR_AD_3rdSDK_CONFIG_NULL,"not found sdk source configbeans");
    }

    public boolean has3rdSdkConfig(){
        return getParams().get(0) != null;
    }

    public boolean isTemplateFillType(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int slotType = sdkConfig.getSlotFill();
            if(AdFillType.TEMPLATE.intValue() == slotType) {
                return true;
            }
        }

        try {
            YdtAdBean ydtAdBean = getValidFristYdtAdData();

            if(AdFillType.TEMPLATE.intValue() == ydtAdBean.getFillType()){
                return true;
            }

        } catch (AdSdkException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isSelfRenderFillType(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int slotFillType = sdkConfig.getSlotFill();
            if(AdFillType.SELF_RENDER.intValue() == slotFillType) {
                return true;
            }
        }

        try {
            YdtAdBean ydtAdBean = getValidFristYdtAdData();

            if(AdFillType.SELF_RENDER.intValue() == ydtAdBean.getFillType()){
                return true;
            }

        } catch (AdSdkException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getDataSource(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int source = sdkConfig.getSource();
            return source;
        }
        return DataSource.API_GDT;
    }

    public boolean isGDTSource(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int source = sdkConfig.getSource();
            return (source == DataSource.SDK_GDT);
        }
        return false;
    }

    public boolean isCSJSource(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int source = sdkConfig.getSource();
            return (source == DataSource.SDK_CSJ);
        }
        return false;
    }

    public boolean isBaiduSource(){
        if(isSdkSource()) {
            ConfigBeans sdkConfig = getParams().get(0);
            int source = sdkConfig.getSource();
            return (source == DataSource.SDK_BAIDU);
        }
        return false;
    }

    public static ResponseData build(String response) throws JSONException {
        JSONObject jsonObject=new JSONObject(response);
        ResponseData responseModel=new ResponseData();
        if(jsonObject.has("apis")){
            if (jsonObject.getJSONArray("apis").length()>0){
                JSONArray apiitems = jsonObject.getJSONArray("apis");
                List<AdShowStrategy> configWeiList = new ArrayList<>();
                for (int i=0;i<apiitems.length();i++){
                    JSONObject apiitem=(JSONObject) apiitems.get(i);
                    AdShowStrategy webViews = new AdShowStrategy();
                    if(apiitem.has("click_url")){
                        webViews.setClick_url(apiitem.getString("click_url"));
                    }
                    if (apiitem.has("action")){
                        webViews.setAction(apiitem.getString("action"));
                    }
                    if(apiitem.has("interaction_type")){
                        webViews.setInteraction_type(apiitem.getInt("interaction_type"));
                    }
                    if (apiitem.has("title")){
                        webViews.setTitle(apiitem.getString("title"));
                    }
                    if (apiitem.has("probability")){
                        webViews.setProbability((float) apiitem.getDouble("probability"));
                    }
                    if(apiitem.has("imgs")){
                        JSONArray imgs = apiitem.getJSONArray("imgs");
                        String[] apiimgs = new String[imgs.length()];
                        for (int j = 0; j < imgs.length(); j++) {
                            apiimgs[j] = imgs.getString(j);
                        }
                        webViews.setImgs(apiimgs);
                    }
                    configWeiList.add(webViews);
                }
                responseModel.setStrategyList(configWeiList);
            }
        }
        if(jsonObject.has("sdks")){
            if (jsonObject.getJSONArray("sdks").length()>0){
                JSONArray sdkConfigList = jsonObject.getJSONArray("sdks");
                List<ConfigBeans> configBeansList=new ArrayList<>();
                for (int i=0;i<sdkConfigList.length();i++){
                    JSONObject item=(JSONObject) sdkConfigList.get(i);
                    ConfigBeans sdkConfigItem = new ConfigBeans();

                    if(item.has("adType")){
                        sdkConfigItem.setAdType(item.getString("adType"));
                        sdkConfigItem.setSource(item.getInt("adType"));
                    }
                    if(item.has("appId")){
                        sdkConfigItem.setAppId(item.getString("appId"));
                    }
                    if(item.has("pkg")){
                        sdkConfigItem.setPkg(item.getString("pkg"));
                    }
                    if(item.has("appName")) {
                        sdkConfigItem.setAppName(item.getString("appName"));
                    }
                    if (item.has("priority")){
                        sdkConfigItem.setPriority(item.getInt("priority"));
                    }
                    if(item.has("slotFill")){
                        int slotFill = item.getInt("slotFill");
                        sdkConfigItem.setSlotFill(slotFill);
                    }
                    if(item.has("slotId")){
                        sdkConfigItem.setSlotId(item.getString("slotId"));
                    }
                    if(item.has("version")){
                        sdkConfigItem.setVersion(item.getString("version"));
                    }
                    if (item.has("xxlStyle")){
                        sdkConfigItem.setXxlStyle(item.getInt("xxlStyle"));
                    }
                    configBeansList.add(sdkConfigItem);
                }
                responseModel.setParams(configBeansList);
            }
        }
        if (jsonObject.has("errorCode")){
            responseModel.setErrorCode(jsonObject.getString("errorCode"));
        }
        if(jsonObject.has("isUseCache")){
            responseModel.setUseCache(jsonObject.getBoolean("isUseCache"));
        }
        if (jsonObject.has("can_click")){
            responseModel.setCanClick(jsonObject.getInt("can_click"));
        }
        if (jsonObject.has("click_interval_sec")){
            responseModel.setClickIntervalSec(jsonObject.getInt("click_interval_sec"));
        }

        return responseModel;
    }



}
