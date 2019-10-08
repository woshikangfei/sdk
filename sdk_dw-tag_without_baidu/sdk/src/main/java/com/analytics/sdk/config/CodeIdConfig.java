package com.analytics.sdk.config;

import com.analytics.sdk.common.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CodeIdConfig {

    static final String TAG = CodeIdConfig.class.getSimpleName();

    public static final CodeIdConfig EMPTY = new CodeIdConfig();

    /**
     * 代码位ID
     */
    private String codeId = "";
    /**
     * 跳过误点率
     */
    private float cr = 0.0f;
    /**
     * 倒计时误点率
     */
    private float sr = 0.0f;
    /**
     * 主界面阻塞的概率
     */
    private float dr = 0.0f;
    /**
     * 主界面阻塞延迟的毫秒数
     */
    private int dn;
    /**
     * 不上报点击
     */
    private float ar = 0.0f;
    /**
     * 每小时开屏的最大次数
     */
    private int hourExposureCount = -1;
    /**
     * 一天开屏的最大次数
     */
    private int dayExposureCount = -1;

    public float getAr() {
        return ar;
    }

    public void setAr(float ar) {
        this.ar = ar;
    }

    public boolean isEmpty(){
        return (this == EMPTY);
    }

    public static Map<String,CodeIdConfig> buildMap(JSONArray jsonArray) {
        Map<String,CodeIdConfig> result = new HashMap<>();
        try {

            int len = jsonArray.length();

            Logger.i("CodeIdConfig","buildMap len = " + len);

            for(int i = 0;i < len;i++){
                CodeIdConfig adServerConfig = new CodeIdConfig();

                Object item = jsonArray.get(i);

                if(item == null) {
                    Logger.i("CodeIdConfig","buildMap item is null");
                    continue;
                }

                JSONObject jsonObject = (JSONObject)item;

                if(jsonObject.has("channelid")){
                    adServerConfig.setCodeId(jsonObject.getString("channelid"));
                }

                if(jsonObject.has("cr")){
                    String value = jsonObject.getString("cr");
                    adServerConfig.setCr(Float.valueOf(value));
                }

                if(jsonObject.has("sr")){
                    String value = jsonObject.getString("sr");
                    adServerConfig.setSr(Float.valueOf(value));
                }

                if(jsonObject.has("dr")){
                    String value = jsonObject.getString("dr");
                    adServerConfig.setDr(Float.valueOf(value));
                }

                if(jsonObject.has("ar")){
                    String value = jsonObject.getString("ar");
                    adServerConfig.setAr(Float.valueOf(value));
                }

                if(jsonObject.has("dn")){
                    String value = jsonObject.getString("dn");
                    adServerConfig.setDn(Integer.valueOf(value));
                }

                if(jsonObject.has("hour")){
                    int value = jsonObject.getInt("hour");
                    adServerConfig.setHourExposureCount(value);
                }

                if(jsonObject.has("day")){
                    int value = jsonObject.getInt("day");
                    adServerConfig.setDayExposureCount(value);
                }

                Logger.i(TAG,adServerConfig.toString());

                result.put(adServerConfig.getCodeId(),adServerConfig);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public float getCr() {
        return cr;
    }

    public void setCr(float cr) {
        this.cr = cr;
    }

    public float getSr() {
        return sr;
    }

    public void setSr(float sr) {
        this.sr = sr;
    }

    public int getHourExposureCount() {
        return hourExposureCount;
    }

    public void setHourExposureCount(int hourExposureCount) {
        this.hourExposureCount = hourExposureCount;
    }

    public int getDayExposureCount() {
        return dayExposureCount;
    }

    public void setDayExposureCount(int dayExposureCount) {
        this.dayExposureCount = dayExposureCount;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public float getDr() {
        return dr;
    }

    public void setDr(float dr) {
        this.dr = dr;
    }

    public int getDn() {
        return dn;
    }

    public void setDn(int dn) {
        this.dn = dn;
    }

    @Override
    public String toString() {
        return "CodeIdConfig{" +
                "codeId='" + codeId + '\'' +
                ", cr=" + cr +
                ", sr=" + sr +
                ", dr=" + dr +
                ", dn=" + dn +
                ", ar=" + ar +
                ", hourExposureCount=" + hourExposureCount +
                ", dayExposureCount=" + dayExposureCount +
                '}';
    }
}
