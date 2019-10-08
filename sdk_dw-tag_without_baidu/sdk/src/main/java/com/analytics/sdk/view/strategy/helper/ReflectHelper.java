package com.analytics.sdk.view.strategy.helper;

import android.view.View;

import java.lang.reflect.Field;

public final class ReflectHelper {

    static Field sViewListenerInfoField = null;
    static Field sViewListenerInfoClickListenerField = null;

    static {
        initViewFields();
    }

    static void initViewFields() {
        if(sViewListenerInfoClickListenerField == null){
            try {
                Class viewClazz = View.class;
                sViewListenerInfoField = viewClazz.getDeclaredField("mListenerInfo");
                sViewListenerInfoField.setAccessible(true);

                Class infoClazz = Class.forName("android.view.View$ListenerInfo");
                sViewListenerInfoClickListenerField = infoClazz.getDeclaredField("mOnClickListener");
                sViewListenerInfoClickListenerField.setAccessible(true);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static View.OnClickListener getListenerInfo(View view) {
        try {
            Object listenerInfoImpl = sViewListenerInfoField.get(view);
            View.OnClickListener onClickListener = (View.OnClickListener) sViewListenerInfoClickListenerField.get(listenerInfoImpl);
            return onClickListener;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
