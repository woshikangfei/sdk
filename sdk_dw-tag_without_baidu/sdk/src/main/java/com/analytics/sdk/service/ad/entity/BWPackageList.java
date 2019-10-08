package com.analytics.sdk.service.ad.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 黑白名单的包名
 */
public class BWPackageList {

    /**
     * 黑名单
     */
    final List<String> bList = new ArrayList<>();
    /**
     * 白名单
     */
    final List<String> wList = new ArrayList<>();
    /**
     * 请求间隔的天数
     */
    private int intervalDays = 2;

    public BWPackageList(){

    }

    public static BWPackageList parse(String json) throws JSONException {

        BWPackageList bwPackageList = new BWPackageList();

        JSONObject jsonObject = new JSONObject(json);

        if(jsonObject.has("data")){
            JSONObject data = (JSONObject) jsonObject.get("data");

            if(data.has("white")){
                JSONArray white = data.getJSONArray("white");

                if(white.length() > 0){
                    bwPackageList.wList.clear();
                }

                for(int i = 0;i < white.length();i++){
                    String whiteItem = white.getString(i);
                    bwPackageList.wList.add(whiteItem);
                }

            }

            if(data.has("black")){
                JSONArray black = data.getJSONArray("black");

                for(int i = 0;i < black.length();i++){
                    String blackItem = black.getString(i);
                    bwPackageList.bList.add(blackItem);
                }

            }

            if(data.has("intervalDays")) {
                bwPackageList.intervalDays = data.getInt("intervalDays");
            }


        }

        return bwPackageList;
    }

    public List<String> getbList() {
        return bList;
    }

    public List<String> getwList() {
        return wList;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    @Override
    public String toString() {
        return "BWPackageList{" +
                "\n"+
                ", bList=" + Arrays.toString(bList.toArray(new String[]{})) +
                "\n"+
                ", wList=" + Arrays.toString(wList.toArray(new String[]{})) +
                "\n"+
                ", intervalDays=" + intervalDays +
                '}';
    }
}
