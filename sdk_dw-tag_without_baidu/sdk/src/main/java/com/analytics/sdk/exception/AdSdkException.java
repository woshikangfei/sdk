package com.analytics.sdk.exception;

import com.analytics.sdk.service.ErrorCode;

public class AdSdkException extends Exception{

    private int code = ErrorCode.NONE;

    public AdSdkException(String msg){
        super(msg);
    }

    public AdSdkException(Throwable t){
        super(t);
    }

    public AdSdkException(int code,String msg){
        super(msg);
        this.code = code;
    }

    public AdSdkException(int code,Throwable t){
        super(t);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
