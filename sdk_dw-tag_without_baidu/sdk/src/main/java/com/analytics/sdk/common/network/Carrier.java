package com.analytics.sdk.common.network;

public enum Carrier {
    UNKNOWN(99),
    CMCC(1),
    TELECOM(2),
    UNICOM(3);

    private int a;

    private Carrier(int value) {
        this.a = value;
    }

    public final int getValue() {
        return this.a;
    }
}

