package com.analytics.sdk.dynamic.common;

/**
 * 所有错误代码
 */
public interface ErrorCode {

    int NONE = -1;

    /**
     * 我们自己服务器
     */
    interface ApiServer {

        /**
         * 服务器正常返回数据了，但是返回的ErrorCode是错误
         */
        int ERROR_SERVER_RESPONSE = 10000;
        /**
         * 连接服务器超时
         */
        int ERROR_SERVER_CONNECT_TIMEOUT = 10001;
        /**
         * 服务器参数错误(为空 或者 {} 这种空值)
         */
        int ERROR_SERVER_PARAMS = 10002;
        /**
         * 解密服务器数据异常
         */
        int ERROR_AES_DECODE_DATA = 10003;
        /**
         * 将JSON解析成对象异常
         */
        int ERROR_PARSE_JSON = 10004;
        /**
         * 未知异常
         */
        int ERROR_UNKNOW = 10005;
        /**
         * 配置为空
         */
        int ERROR_AD_3rdSDK_CONFIG_NULL = 10006;
        /**
         * 找不到对应的处理资源
         */
        int ERROR_AD_SOURCE_NOT_FOUND = 10007;
    }

    interface OS {
        /**
         * 权限错误
         */
        int ERROR_PERMISSION = 20000;
        /**
         * 当前没有网络
         */
        int ERROR_NETWORK_DISCONNECTED = 20001;
        /**
         * 请求已经被释放
         */
        int ERROR_AD_REQUEST_RECYCLED = 20002;
    }

    interface Http {
        /**
         * 未知错误
         */
        int ERROR_HTTP_UNKNOW = 40000;
        /**
         * 超时
         */
        int ERROR_HTTP_TIMEOUT = 40001;
        /**
         * 无连接
         */
        int ERROR_HTTP_NO_CONNECT = 40002;
        /**
         * http code 服务器错误(500等)
         */
        int ERROR_HTTP_SERVER = 40003;
        /**
         * 没有从流中读取到任何内容
         */
        int ERROR_HTTP_NETWORK = 40004;
    }

    /**
     * 通过获取自己服务器得到的广告数据
     */
    interface Api {
        /**
         * 无广告
         */
        int ERROR_NO_AD = 50000;
        /**
         * 广告超时
         */
        int ERROR_TIMEOUT = 50001;
        /**
         * 没有找到对应的三方SDK处理
         */
        int ERROR_NOTFOUND_SOURCE = 50002;
        /**
         * 处理异常
         */
        int ERROR_HANDLE = 50003;
        /**
         * 广告图片加载失败
         */
        int ERROR_LOAD_AD_IMAGE = 50004;
    }

    /**
     * 广点通
     */
    interface SDKGDT {
        /**
         * 无广告
         */
        int ERROR_NO_AD = 60000;
        /**
         * 广告超时
         */
        int ERROR_TIMEOUT = 60001;
        /**
         * 处理异常
         */
        int ERROR_HANDLE = 60002;
        /**
         * 成功返回了，但是返回的数据为空
         */
        int ERROR_RESPONSE_DATA_EMPTY = 60003;
        /**
         * 广告图片资源加载失败
         */
        int ERROR_LOAD_AD_IMAGE = 60004;

    }

    /**
     * 广点通
     */
    interface SDKCSJ {
        /**
         * 无广告
         */
        int ERROR_NO_AD = 70000;
        /**
         * 广告超时
         */
        int ERROR_TIMEOUT = 70001;
        /**
         * 处理异常
         */
        int ERROR_HANDLE = 70002;
        /**
         * 成功返回了，但是返回的数据为空
         */
        int ERROR_RESPONSE_DATA_EMPTY = 70003;
        /**
         * 广告图片资源加载失败
         */
        int ERROR_LOAD_AD_IMAGE = 70004;

    }

    /**
     * 广点通
     */
    interface SDKBaidu {
        /**
         * 无广告
         */
        int ERROR_NO_AD = 80000;
    }

    /**
     * 补丁包
     */
    interface HOTFIX {
        /**
         * 获取下载信息失败
         */
        int ERROR_FETCH_HOTFIX_INFO = 90000;
        /**
         *  补丁包下载失败
         */
        int ERROR_DOWNLOAD = 90001;
        /**
         * 热修复失败
         */
        int ERROR_HOTFIX = 90002;

    }

}
