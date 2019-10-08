package com.analytics.sdk.client;

/**
 * 客户端的配置
 */
public class SdkConfiguration {

    public static final SdkConfiguration DEFAULT = new SdkConfiguration();

    private SdkConfiguration(){
    }

    /**
     * 当前应用的名称
     */
    private String appName;

    public String getAppName() {
        return appName;
    }

    public static class Builder {

        private String appName;

        public Builder(){
        }

        public Builder(SdkConfiguration adSdkConfiguration){
            this.appName = adSdkConfiguration.getAppName();
        }

        public Builder setAppName(String appName){
            this.appName = appName;
            return this;
        }

        public SdkConfiguration build(){
            SdkConfiguration sdkConfiguration = new SdkConfiguration();
            sdkConfiguration.appName = this.appName;
            return sdkConfiguration;
        }

    }

    @Override
    public String toString() {
        return "SdkConfiguration{" +
                "appName='" + appName + '\'' +
                '}';
    }
}
