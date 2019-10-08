/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.analytics.sdk.common.image;

import android.graphics.Bitmap;
import android.os.Build;

import com.analytics.sdk.common.image.pool.BitmapPool;
import com.analytics.sdk.common.image.pool.BitmapPoolAdapter;
import com.analytics.sdk.common.image.pool.LruBitmapPool;

import java.util.Set;

/**
 * Created by amitshekhar on 17/06/16.
 */
public class ImagePool {

    private static final int DEFAULT_MAX_SIZE = 6 * 1024 * 1024;
    private BitmapPool bitmapPool;
    private static ImagePool sInstance;

    private ImagePool(int maxSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bitmapPool = new LruBitmapPool(maxSize);
        } else {
            bitmapPool = new BitmapPoolAdapter();
        }
    }

    private ImagePool(int maxSize, Set<Bitmap.Config> allowedConfigs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bitmapPool = new LruBitmapPool(maxSize, allowedConfigs);
        } else {
            bitmapPool = new BitmapPoolAdapter();
        }
    }

    private static ImagePool getInstance() {
        if (sInstance == null) {
            sInstance = new ImagePool(DEFAULT_MAX_SIZE);
        }
        return sInstance;
    }

    public static void initialize(int maxSize) {
        sInstance = new ImagePool(maxSize);
    }

    public static void initialize(int maxSize, Set<Bitmap.Config> allowedConfigs) {
        sInstance = new ImagePool(maxSize, allowedConfigs);
    }

    public static void putBitmap(Bitmap bitmap) {
        getInstance().bitmapPool.put(bitmap);
    }

    public static Bitmap getBitmap(int width, int height, Bitmap.Config config) {
        return getInstance().bitmapPool.get(width, height, config);
    }

    public static Bitmap getDirtyBitmap(int width, int height, Bitmap.Config config) {
        return getInstance().bitmapPool.getDirty(width, height, config);
    }

    public static void clearMemory() {
        getInstance().bitmapPool.clearMemory();
    }

    public static void trimMemory(int level) {
        getInstance().bitmapPool.trimMemory(level);
    }

}
