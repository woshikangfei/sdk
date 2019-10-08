package com.analytics.sdk.common.helper;

import com.analytics.sdk.common.http.error.NetworkError;
import com.analytics.sdk.common.http.error.NoConnectionError;
import com.analytics.sdk.common.http.error.ServerError;
import com.analytics.sdk.common.http.error.TimeoutError;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.ErrorCode;

public abstract class Listener<SuccessDataType,ErrorDataType> {

    public static final Listener EMPTY = new Listener() {};

    public static final Listener<String, String> ONLY_LOG = new Listener<String, String>() {

        static final String logTag = "onlyLog";

        @Override
        public boolean onSuccess(SuccessMessage<String> successMessage) {
            Logger.i(logTag,"onSuccess enter , message = " + successMessage.getMessage());
            return false;
        }

        @Override
        public boolean onError(ErrorMessage<String> errorMessage) {
            Logger.i(logTag,"onError enter , message = " + errorMessage.getMessage());
            return false;
        }
    };

    public boolean onSuccess(SuccessMessage<SuccessDataType> successMessage){
        return false;
    }

    /**
     * 一定要调用super方法
     */
    public boolean onError(ErrorMessage<ErrorDataType> errorMessage){
        return false;
    }

    public String errorNotifier(VolleyError error) {
        if(error instanceof TimeoutError){
            onError(Listener.ErrorMessage.obtain(ErrorCode.Http.ERROR_HTTP_TIMEOUT,"连接超时"));
            return "TimeoutError";
        } else if(error instanceof NoConnectionError) {
            onError(Listener.ErrorMessage.obtain(ErrorCode.Http.ERROR_HTTP_NO_CONNECT,"连接服务器失败"));
            return "NoConnectionError";
        } else if(error instanceof ServerError) {
            onError(Listener.ErrorMessage.obtain(ErrorCode.Http.ERROR_HTTP_SERVER,"服务器异常"));
            return "ServerError";
        } else if(error instanceof NetworkError) {
            onError(Listener.ErrorMessage.obtain(ErrorCode.Http.ERROR_HTTP_NETWORK,"网络错误"));
            return "NetworkError";
        } else {
            onError(Listener.ErrorMessage.obtain(ErrorCode.Http.ERROR_HTTP_UNKNOW,"未知错误"));
            return "UNKNOW";
        }
    }


    public static class BaseMessage<T> {
        protected String message;
        protected T data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public static class SuccessMessage<T> extends BaseMessage<T>{

        public static final String OK = "OK";

        public static <T> SuccessMessage obtain(T data){
            return obtain(OK,data);
        }

        public static <T> SuccessMessage obtain(String message,T data){
            SuccessMessage<T> successMessage = new SuccessMessage<T>();
            successMessage.setMessage(message);
            successMessage.setData(data);
            return successMessage;
        }

    }

    public static class ErrorMessage<T> extends BaseMessage<T>{
        private int code;

        public static ErrorMessage obtain(int code,String message){
            return obtain(code,message,null);
        }

        public static <T> ErrorMessage obtain(int code,String message,T tag){
            ErrorMessage<T> errorMessage = new ErrorMessage<T>();
            errorMessage.code = code;
            errorMessage.setMessage(message);
            errorMessage.setData(tag);
            return errorMessage;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "ErrorMessage{" +
                    "message='" + message + '\'' +
                    ", data=" + data +
                    ", code=" + code +
                    '}';
        }
    }

}
