package com.analytics.sdk.common.network;


public enum NetworkType {
    UNKNOWN(0, 1, "unknown"),
    WIFI(1, 2, "wifi"),
    NET_2G(2, 4, "2g"),
    NET_3G(3, 8, "3g"),
    NET_4G(4, 16, "4g");

    private int a;
    private int b;
    private String c;

    private NetworkType(int connValue, int permValue, String nameValue) {
        this.a = connValue;
        this.b = permValue;
        this.c = nameValue;
    }

    public final int getConnValue() {
        return this.a;
    }

    public final int getPermValue() {
        return this.b;
    }

    public final String getNameValue() {
        return this.c;
    }
}

