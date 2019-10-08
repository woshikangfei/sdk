package com.analytics.sdk.config;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ad.ISpamService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SDK初始化时从服务器获取到的数据
 */
public class ServerInitConfig {

    public static final ServerInitConfig EMPTY = new ServerInitConfig();

    private int code = -1;

    private String[] dcdgroup;

    public String[] getDcdgroup() {
        return dcdgroup;
    }

    public void setDcdgroup(String[] dcdgroup) {
        this.dcdgroup = dcdgroup;
    }

    /**
     * 反作弊是否开启
     */
    volatile int spam = BuildConfig.SPAM_ENABLE;
    /**
     * 当前设备的作弊状态
     * 0 nothing
     * 1 灰
     * 2 黑
     */
    volatile int deviceSpamState = ISpamService.SpamState.STATE_NOTING;
    /**
     * 开屏是否可以点击
     */
    volatile int splashCanClick = 0;
    /**
     * 信息流是否可以点击
     */
    volatile int feedlistCanClick = 0;
    /**
     * 是否使用缓存
     */
    volatile boolean isUseCache = true;
    /**
     * 是否强制走服务器
     */
    volatile boolean isForceRequestServer = false;

    /**
     * map.key 广告位ID
     * map.value 这个广告位配置的误点相关
     */
    final Map<String,CodeIdConfig> adServerConfigMapping = new ConcurrentHashMap<>();

    public int getDeviceSpamState() {
        return deviceSpamState;
    }

    public int getSplashCanClick() {
        return splashCanClick;
    }

    public int getFeedlistCanClick() {
        return feedlistCanClick;
    }

    public Map<String, CodeIdConfig> getAdServerConfigMapping() {
        return adServerConfigMapping;
    }

    public CodeIdConfig getCodeIdConfig(String codeId){
        return getAdServerConfigMapping().get(codeId);
    }

    public boolean isSupportSpam(){
        return spam == 1;
    }

    public static ServerInitConfig parse(String jsonData) throws JSONException {

        ServerInitConfig adData = new ServerInitConfig();
        JSONObject adJsonData = new JSONObject(jsonData);

        if(adJsonData.has("code")){
            adData.code = adJsonData.getInt("code");
        }

        JSONObject data = null;

        if(adJsonData.has("data")){
            data = adJsonData.getJSONObject("data");
        }

        if(adData.isOk() && data != null) {

            if(data.has("ps")){
                JSONArray jsonArray = data.getJSONArray("ps");
                Map<String,CodeIdConfig> adServerConfigMap = CodeIdConfig.buildMap(jsonArray);
                adData.adServerConfigMapping.putAll(adServerConfigMap);
            }
            if(data.has("spam")) {
                adData.spam = data.getInt("spam");
            }
            if(data.has("black")) {
                adData.deviceSpamState = data.getInt("black");
            }
            if(data.has(AdType.SPLASH.getStringValue())) {
                adData.splashCanClick = data.getInt(AdType.SPLASH.getStringValue());
            }
            if(data.has(AdType.INFORMATION_FLOW.getStringValue())) {
                adData.feedlistCanClick = data.getInt(AdType.INFORMATION_FLOW.getStringValue());
            }
            if(data.has("dcdgroup")){
                JSONArray dcdgroup = data.getJSONArray("dcdgroup");
                int length = dcdgroup.length();
                Logger.forcePrint("dcdgroup.length", length+"");
                adData.dcdgroup = new String[dcdgroup.length()];
                for (int i = 0; i < dcdgroup.length(); i++) {
                    String string = dcdgroup.getString(i);
                    adData.dcdgroup[i] = string;
                }
            }
        } else if(adData.code == -1002){
            adData.isUseCache = false;
        } else if(adData.code == -1003){
            adData.isForceRequestServer = true;
        }

        return adData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isOk(){
        return code == 1;
    }

    public boolean isClearPSCache(){
        return code == -1000;
    }

    public boolean isForceRequestServer() {
        return isForceRequestServer;
    }

    public void setForceRequestServer(boolean forceRequestServer) {
        isForceRequestServer = forceRequestServer;
    }

    public boolean isClearSpamCache(){
        return code == -1001;
    }

    public boolean isUseCache() {
        return isUseCache;
    }

    public void setUseCache(boolean useCache) {
        isUseCache = useCache;
    }

    public void clearSpam(){
        deviceSpamState = ISpamService.SpamState.STATE_NOTING;
        splashCanClick = 0;
        feedlistCanClick = 0;
    }

    public boolean isModify(){
        return code == -1;
    }

    public void clearServerAdConfig() {
        adServerConfigMapping.clear();
    }

    @Override
    public String toString() {

        StringBuilder codeIdBuilder = new StringBuilder();

        for(Iterator<Map.Entry<String,CodeIdConfig>> iter = adServerConfigMapping.entrySet().iterator();iter.hasNext();){
            codeIdBuilder.append(iter.next().getValue().toString()).append("\n");
        }

        return "ServerInitConfig{" +
                "code=" + code +
                ", spam=" + spam +
                ", deviceSpamState=" + deviceSpamState +
                ", splashCanClick=" + splashCanClick +
                ", feedlistCanClick=" + feedlistCanClick +
                ", \n " +
                ", map = " + codeIdBuilder.toString() +
                '}';
    }
}
