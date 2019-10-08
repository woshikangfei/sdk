package com.analytics.sdk.common.cache;

import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.LinkedHashMap;


public class LruCacheSimple<Key,Value> {

    static final String TAG = LruCacheSimple.class.getSimpleName();

    private static final int DEFAULT_LIMIT_SIZE = 10;

    private int mLinitSize = DEFAULT_LIMIT_SIZE;

    final LruCache<Key,Value> mStack;

    public LruCacheSimple(){
        this(DEFAULT_LIMIT_SIZE);
    }

    public LruCacheSimple(int limitSize){
        this.mLinitSize = limitSize;
        mStack = new LruCache<Key,Value>(limitSize){
            @Override
            protected void entryRemoved(boolean evicted, Key key, Value oldValue, Value newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                try {
                    Log.i(TAG,"evicted = " + evicted +" ,entry removed = " + key + " , cache size = " + mStack.size());
                    if (evicted && key != null) {
                        Log.i(TAG,"entry removed and finish");
                    }
                } catch (Exception e){
                    //ignore
                }

            }
        };
    }

    public void put(Key k,Value v) {
        mStack.put(k, v);
    }

    public void clear(){
        mStack.evictAll();
    }

    public void remove(Key k){
        mStack.remove(k);
    }

    public LinkedHashMap<Key,Value> snapshot(){
        return (LinkedHashMap<Key, Value>) mStack.snapshot();
    }

    public boolean hasCacheItems(){
        return (mStack == null ? false : (mStack.size() > 0));
    }

    public int cacheSize(){
        return (mStack == null ? 0 : (mStack.size()));
    }

    public Value get(Key cacheKey) {
        return mStack.get(cacheKey);
    }
}
