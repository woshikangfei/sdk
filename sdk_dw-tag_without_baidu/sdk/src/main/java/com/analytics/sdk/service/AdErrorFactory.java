package com.analytics.sdk.service;

import com.analytics.sdk.client.AdError;

public abstract class AdErrorFactory {

    public abstract AdError create(int errorCode);

    public static AdErrorFactory factory(){
        return DEFAULT_IMPL;
    }

    static final AdErrorFactory DEFAULT_IMPL = new AdErrorFactory() {

        public AdError create(int errorCode) {

            switch (errorCode) {

                case ErrorCode.RewardVideo.ERROR_VIDEO_SHOWN:
                    return new AdError(errorCode,"此条广告已经展示过，请再次请求广告后进行广告展示！");
                case ErrorCode.RewardVideo.ERROR_VIDEO_COMPLETED_SHOWN:
                    return new AdError(errorCode,"成功加载广告后再进行广告展示！");
                case ErrorCode.RewardVideo.ERROR_VIDEO_LOAD:
                    return new AdError(errorCode,"加载视频失败！");
                case ErrorCode.SPAM:
                    return new AdError(ErrorCode.SPAM,"广告无填充!");
                case ErrorCode.Api.ERROR_NO_AD:
                    return new AdError(errorCode,"广告无填充!");
                case ErrorCode.Api.ERROR_AD_CONTAINER_DESTORY:
                    return new AdError(errorCode,"容器已销毁!");
                case ErrorCode.Api.ERROR_AD_IMAGE:
                    return new AdError(errorCode,"图片加载失败!");
                default:
                    return AdError.EMPTY;

            }

        }
    };

}
