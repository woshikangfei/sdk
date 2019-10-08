package com.analytics.sdk.common.cache;

import android.text.TextUtils;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.data.DataProvider;
import com.analytics.sdk.common.log.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CacheSpSimple implements ICache{

    static final String TAG = CacheSpSimple.class.getSimpleName();

    DataProvider cacheProvider;

    public CacheSpSimple() {
         cacheProvider = DataProvider.newProvider(AdClientContext.getClientContext(),"CacheSpSimple");
    }

    @Override
    public String getAsString(String key) {
        long[] saveTime = getSaveTime(key);
        Logger.i(TAG,"getAsString enter , key = " + key);
        if(saveTime == null){
            return cacheProvider.getString(key,null);
        } else {

            long currentDateTime = System.currentTimeMillis();

            long saveSenconds = saveTime[0];
            long saveDateTime = saveTime[1];

            long diff = currentDateTime - saveDateTime;

            Logger.i(TAG,key + " , saveSenconds = " + saveSenconds + " , diff = " + diff);

            if(diff > saveSenconds * 1000){
                Logger.i(TAG,"cache expire("+key+")");
                remove(key);
                return null;
            } else {
                Logger.i(TAG,"cache hit("+key+")");
                return cacheProvider.getString(key,null);
            }

        }

    }

    @Override
    public void put(String key, String value) {
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        Logger.i(TAG,"put enter , key = " + key);
        cacheProvider.insert(key, value);
    }

    @Override
    public void put(String key, String value, int saveTime) {
        if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        Logger.i(TAG,"put2 enter , key = " + key + " , saveTime = " + saveTime);
        long saveDataTime = System.currentTimeMillis();
        cacheProvider.insert(buildSaveTimeKey(key),saveTime+"_"+saveDataTime);
        cacheProvider.insert(key, value);
    }

    private long[] getSaveTime(String key){

        String time = cacheProvider.getString(buildSaveTimeKey(key),"");
        if(TextUtils.isEmpty(time)) {
            return null;
        }

        if(time.contains("_")) {
            String[] timeArray = time.split("_");
            if(timeArray == null || (timeArray != null && timeArray.length == 0)){
                return null;
            }
            String saveSeconds = timeArray[0];
            String saveDateTime = timeArray[1];

            if(TextUtils.isEmpty(saveSeconds) || TextUtils.isEmpty(saveDateTime)) {
                return null;
            }

            long saveSecondsL = Long.valueOf(saveSeconds);
            long saveDateTimeL = Long.valueOf(saveDateTime);

            return new long[] {saveSecondsL,saveDateTimeL};

        }

        return null;
    }

    private String buildSaveTimeKey(String key){
        return key+"_savetime";
    }

    @Override
    public void clear() {
        cacheProvider.deleteAll();
    }

    @Override
    public boolean remove(String key) {
        cacheProvider.delete(key);
        cacheProvider.delete(buildSaveTimeKey(key));
        return true;
    }

    @Override
    public File getCacheDir() {
        return cacheProvider.getProviderFile();
    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<>();

        Map<String,Object> map = cacheProvider.all();

        if(map.size() > 0){
            for(Iterator<String> iter = map.keySet().iterator();iter.hasNext();){
                String key = iter.next();
                Object value = map.get(key);
                keys.add(key);
            }
        }

        return keys;
    }
}
