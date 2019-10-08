package com.analytics.sdk.dynamic.common;

public interface ErrorMessage {

    interface Ad {
        String ERROR_NO_AD = "无广告";
        String ERROR_IMAGE_NOT_FOUND = "找不到广告图片";
        String ERROR_GET_IMAGE = "获取广告图片失败";
        String ERROR_GET_ADS = "获取广告失败";
        String ERROR_GET_ADS_TIMEOUT = "获取广告超时";
        String ERROR_DATA_EMPTY = "广告数据为空";
        String ERROR_HANDLE = "内部处理异常";
    }

    interface OS {
        String ERROR_PERMISSION = "没有权限";
        String ERROR_NETWORK_DISCONNECTED = "无网络";
        String ERROR_ADREQUEST_RECYCLED = "请求无效,资源已被释放";
    }


}
