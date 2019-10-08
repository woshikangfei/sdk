package com.analytics.sdk.common.cache;

import java.io.File;
import java.util.List;

public interface ICache {

    String getAsString(String key) ;
    void put(String key, String value);
    void put(String key, String value, int saveTime) ;
    void clear() ;
    boolean remove(String key) ;
    File getCacheDir() ;
    List<String> listKeys();
    
}
