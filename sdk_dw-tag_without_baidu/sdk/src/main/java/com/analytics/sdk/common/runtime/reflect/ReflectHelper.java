package com.analytics.sdk.common.runtime.reflect;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectHelper {

    static final String TAG = "ReflectHelper";

    public static Object getFieldNoException(Class clazz, Object target, String name) {
        try {
            return getField(clazz, target, name);
        } catch (Exception e) {
            //ignored.
            Log.i(TAG,"getFieldNoException exception = " + e.getMessage());
        }

        return null;
    }


    public static void setField(Class clazz, Object target, String name, Object value) throws Exception {
        Field field = findField(clazz,name);
        field.setAccessible(true);
        field.set(target, value);
    }

    static Field findField(Class clazz,String name) throws NoSuchFieldException {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
                try {
                    return cls.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    // Ignored
                }
            }
            throw e;
        }
    }

    public static void setFieldNoException(Class clazz, Object target, String name, Object value) {
        try {
            setField(clazz, target, name, value);
        } catch (Exception e) {
            // ignored.
            Log.i(TAG,"setFieldNoException name = " + name + " , value = " + value);
        }
    }

    public static Object invoke(Class clazz, Object target, String name, Object... args)
            throws Exception {
        Class[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }

        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    @SuppressWarnings("unchecked")
    public static Object invoke(Class clazz, Object target, String name, Class[] parameterTypes, Object... args)
            throws Exception {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    public static Object invokeNoException(Class clazz, Object target, String name, Class[] parameterTypes, Object... args) {
        try {
            return invoke(clazz, target, name, parameterTypes, args);
        } catch (Exception e) {
        }

        return null;
    }

    public static Object getField(Class clazz, Object target, String name) throws Exception {
        Field field = findField(clazz,name);
        field.setAccessible(true);
        return field.get(target);
    }

    public static Object invokeConstructor(Class clazz, Class[] parameterTypes, Object... args)
            throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

}
