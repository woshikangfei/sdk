package com.analytics.sdk.dynamic.a;

public class AdEvent {

    public static final int EVNET_UNKNOW = -1;
    /**
     * 点击
     */
    public static final int EVENT_CLICK = EVNET_UNKNOW + 1;
    public static final int EVENT_DISMISSED = EVENT_CLICK + 1;
    public static final int EVENT_SHOW = EVENT_DISMISSED + 1;
    //onADExposure
    /**
     * 曝光
     */
    public static final int EVENT_EXPOSURE = EVENT_SHOW + 1;
    /**
     * 错误
     */
    public static final int EVENT_ERROR = EVENT_EXPOSURE + 1;
    /**
     * 倒计时
     */
    public static final int EVENT_TICK = EVENT_ERROR + 1;

    public static String getEventString(int event){
        switch (event) {
            case EVENT_DISMISSED:
                return "EVENT_DISMISSED";

            case EVENT_ERROR:
                return "EVENT_ERROR";

            case EVENT_EXPOSURE:
                return "EVENT_EXPOSURE";

            case EVENT_SHOW:
                return "EVENT_SHOW";

            case EVENT_CLICK:
                return "EVENT_CLICK";

            case EVENT_TICK:
                return "EVENT_TICK";

            default:
                return "UNKNOW";
        }
    }

}
