package com.analytics.sdk.service.dynamic;

import android.content.Context;

import com.analytics.sdk.service.IService;

import java.io.File;
import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;

public interface IDynamicContext {

    Context getAppContext();
    DexClassLoader getDynamicDexClassLoader();
    File getDynamicHotfixFile();
    File getDynamicTaskFile();
    File getDynamicDir();
    Map<Class<?>, Class<?>> getHotfixClassMapping();
    List<Class<?>> getTaskClassList();
    Map<Class<? extends IService>,IService> getHotfixImpl();
    List<IService> getTaskImplList();

}
