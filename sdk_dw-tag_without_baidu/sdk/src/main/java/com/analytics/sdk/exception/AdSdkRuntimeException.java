package com.analytics.sdk.exception;

public class AdSdkRuntimeException extends RuntimeException{

    public AdSdkRuntimeException(String msg){
        super(msg);
    }

    public AdSdkRuntimeException(Throwable t){
        super(t);
    }

}
