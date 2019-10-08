package com.analytics.sdk.common.image;

public class ImageDecodeException extends Exception {
    public ImageDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
    public ImageDecodeException(Throwable cause) {
        super(cause);
    }
}
