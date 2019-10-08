package com.analytics.sdk.common.image;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.analytics.sdk.BuildConfig;
import com.analytics.sdk.common.log.Logger;

public class ImageLruCache extends LruCache<String,ImageLruCache.CacheNode> {

    private static final String TAG = "ImageLruCache";
    static ImageLruCache DEFAULT;

    public static ImageLruCache getDefault(){
        if(DEFAULT == null){
            long maxMemory = Runtime.getRuntime().maxMemory();
            int cacheSize = (int) (maxMemory / 8);
            DEFAULT = new ImageLruCache(cacheSize);
        }
        return DEFAULT;
    }

    public ImageLruCache(int maxSize) {
        super(maxSize);
        Logger.i(TAG,"cacheSize = " + maxSize + ", isDebug = " + BuildConfig.DEBUG);
    }

    protected void onItemEvicted(@NonNull String key, @Nullable CacheNode item) {
        Logger.i(TAG, "onItemEvicted enter, item = " + item.getLifeTime());
        CacheNode cacheNode = get(item.url);
//        ImagePool.putBitmap(cacheNode.value);
        Logger.i(TAG, "onItemEvicted enter, cacheNode = " + cacheNode);
    }

    protected int getSize(@Nullable CacheNode item) {
        if (item == null) {
            return super.getSize(null);
        } else {
            return item.getSize();
        }
    }

    @SuppressLint("InlinedApi")
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
                || level == android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            trimToSize(getMaxSize() / 2);
        }
    }

    // 把Bitmap对象加入到缓存中
    public void addBitmapToMemory(final String key, final Bitmap bitmap) {
        Logger.i(TAG, "addBitmapToMemory enter , bitmap = " + (bitmap != null ? (ImageHelper.getBitmapByteSize(bitmap)) : "null"));
        if (get(key) == null) {
            put(key, new CacheNode(key,bitmap));
        }
    }

    // 从缓存中得到Bitmap对象
    public Bitmap getBitmapFromMemCache(String key) {
        Logger.i(TAG, "getBitmapFromMemCache lrucache size: "+getCurrentSize()/1024);
        CacheNode cacheNode = get(key);
        if(cacheNode!=null){
            return cacheNode.value;
        }
        return null;
    }

    static class CacheNode {
        String url;
        long cacheTime;
        Bitmap value;
        public CacheNode(String url, Bitmap value){
            this.url = url;
            cacheTime = System.currentTimeMillis();
            this.value = value;
        }

        public int getSize(){
            return ImageHelper.getBitmapByteSize(value);
        }

        /**
         * 获取存活时间，单位 S
         */
        public long getLifeTime(){
            return ((System.currentTimeMillis() - cacheTime) / 1000);
        }

        public void recycle(){
            if(value!=null){
                value.recycle();
                value = null;
            }
        }

    }

}
