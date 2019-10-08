package com.analytics.sdk.client;

import com.analytics.sdk.service.ErrorCode;

public final class AdError {

    public static final AdError EMPTY = new AdError(ErrorCode.NONE,"");

    private int errorCode;
    private String errorMessage;
    private String extMessage;

    public AdError(int errorCode,String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AdError(int errorCode,String errorMessage,String extMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.extMessage = extMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "AdError{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", extMessage='" + extMessage + '\'' +
                '}';
    }

    public String getExtMessage(){
        return extMessage;
    }

    public boolean isSelfApiError() {
        return (errorCode >= 10000 && errorCode < 20000);
    }
}
