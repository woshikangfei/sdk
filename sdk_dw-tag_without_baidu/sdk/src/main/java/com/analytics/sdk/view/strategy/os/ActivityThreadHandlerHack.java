package com.analytics.sdk.view.strategy.os;

import android.os.Handler;
import android.os.Message;

import com.analytics.sdk.common.log.Logger;

public class ActivityThreadHandlerHack implements Handler.Callback {

    static final String TAG = "ActivityThreadHandlerHack";

    public static final int LAUNCH_ACTIVITY         = 100;
    public static final int PAUSE_ACTIVITY          = 101;
    public static final int PAUSE_ACTIVITY_FINISHING= 102;
    public static final int STOP_ACTIVITY_SHOW      = 103;
    public static final int STOP_ACTIVITY_HIDE      = 104;
    public static final int SHOW_WINDOW             = 105;
    public static final int HIDE_WINDOW             = 106;
    public static final int RESUME_ACTIVITY         = 107;
    public static final int SEND_RESULT             = 108;
    public static final int DESTROY_ACTIVITY        = 109;

    String getWhatString(int what){
        switch (what) {
            case LAUNCH_ACTIVITY:
                return "LAUNCH_ACTIVITY";
            case PAUSE_ACTIVITY:
                return "PAUSE_ACTIVITY";
            case RESUME_ACTIVITY:
                return "RESUME_ACTIVITY";
            case DESTROY_ACTIVITY:
                return "DESTROY_ACTIVITY";
            case SHOW_WINDOW:
                return "SHOW_WINDOW";
        }
        return "other";
    }


    @Override
    public boolean handleMessage(Message message) {

        int what = message.what;

        String whatString = getWhatString(what);

        if(!"other".equals(whatString)) {
            Logger.i(TAG,"hack handleMessage whatString = " + getWhatString(what));
        }

        if(what == DESTROY_ACTIVITY) {
            return false; //do nothing
        }

        return false;
    }
}
