package com.analytics.sdk.common.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.util.Map;

public class DefaultDataProviderImpl extends DataProvider{

    private String PREFERENCES_FILE_NAME = "devyok.DATA_PROVIDER";

    private Context mContext;


    public DefaultDataProviderImpl(Context context){
        this.mContext = context;
    }

    public DefaultDataProviderImpl(Context context,String dataSourceName){
        this.PREFERENCES_FILE_NAME = dataSourceName;
    }

    @Override
    public void insert(String key, String value) {
        getDataSource().edit().putString(key,value).commit();
    }

    @Override
    public void insertInt(String key, int value) {
        getDataSource().edit().putInt(key,value).commit();
    }

    @Override
    public void insertBoolean(String key, boolean value) {
        getDataSource().edit().putBoolean(key,value).commit();
    }

    @Override
    public void update(String key, String value) {
        //apply 是异步的 commit是同步的会阻塞ui 需要换吗？
        getDataSource().edit().putString(key,value).apply();
    }

    @Override
    public String get(String key) {
        return getDataSource().getString(key,"");
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getDataSource().getString(key,defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getDataSource().getBoolean(key,defaultValue);
    }

    @Override
    public int getInt(String key,int defaultValue) {
        return getDataSource().getInt(key,defaultValue);
    }

    @Override
    public boolean has(String key) {
        return getDataSource().contains(key);
    }

    @Override
    public int size() {
        return all().size();
    }

    @Override
    public void delete(Map<String, Object> dataMapping) {
        if(dataMapping != null && dataMapping.size() > 0){
            for (Map.Entry<String, Object> stringObjectEntry : dataMapping.entrySet()) {
                String key = stringObjectEntry.getKey();
                delete(key);
            }
        }
    }

    @Override
    public boolean delete(String key) {
        return getDataSource().edit().remove(key).commit();
    }

    @Override
    public Map<String, Object> all() {
        return (Map<String, Object>) getDataSource().getAll();
    }

    @Override
    public void deleteAll() {
        getDataSource().edit().clear().commit();
    }

    @Override
    public void setDataSourceName(String dsn) {
        PREFERENCES_FILE_NAME = dsn;
    }

    @Override
    public String getDataSourceName() {
        return PREFERENCES_FILE_NAME;
    }

    @Override
    public DataProvider startLoad() {
        getDataSource();
        return this;
    }

    @Override
    public File getProviderFile() {
        String dataDir = mContext.getApplicationInfo().dataDir;
        File preferencesDir = new File(dataDir, "shared_prefs");
        File providerFile = new File(preferencesDir,PREFERENCES_FILE_NAME+".xml");
        return providerFile;
    }

    SharedPreferences getDataSource(){
        return mContext.getSharedPreferences(PREFERENCES_FILE_NAME,Context.MODE_PRIVATE);
    }

}
