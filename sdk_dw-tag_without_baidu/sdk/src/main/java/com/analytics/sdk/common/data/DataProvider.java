package com.analytics.sdk.common.data;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Map;

public abstract class DataProvider {

    static DataProvider DEFAULT;

    public abstract void insert(String key,String value);
    public abstract void insertInt(String key,int value);
    public abstract void insertBoolean(String key,boolean value);
    public abstract void update(String key,String value);
    public abstract String get(String key);
    public abstract String getString(String key,String defaultValue);
    public abstract boolean getBoolean(String key,boolean defaultValue);
    public abstract int getInt(String key,int defaultValue);
    public abstract boolean has(String key);
    public abstract int size();
    public abstract void delete(Map<String,Object> map);
    public abstract boolean delete(String key);
    public abstract Map<String,Object> all();
    public abstract void deleteAll();
    public abstract void setDataSourceName(String dsn);
    public abstract String getDataSourceName();
    public abstract DataProvider startLoad();
    public abstract File getProviderFile();

    public static void initDefault(Context context){
        if(DEFAULT == null){
            DEFAULT = new DefaultDataProviderImpl(context);
        }
    }

    public static DataProvider getDefault(){
        if(DEFAULT == null){
            throw new RuntimeException("must call initDefault method");
        }
        return DEFAULT;
    }

    public static DataProvider newProvider(Context context,String datasourceName){
        DataProvider dataProvider = new DefaultDataProviderImpl(context);
        dataProvider.setDataSourceName(datasourceName);
        return dataProvider;
    }


    public static void dump(String logPrefix,DataProvider dataProvider){
        Map<String,Object> dataMapping = dataProvider.all();
        if(dataMapping!=null){
            for (Map.Entry<String, Object> stringObjectEntry : dataMapping.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                Log.i("DataProvider",logPrefix + "dataProvider key = " + key + " , value = " + value);
            }
        }
    }

}
