package com.analytics.sdk.service.ad.entity;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.helper.AppHelper;
import com.analytics.sdk.common.helper.DeviceHelper;
import com.analytics.sdk.common.helper.UIHelper;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class AdRequestParameters {
    private String requestId;
    //渠道ID
    private String channelId;
    private int sdktype;

    //广告类型
    private int adType;
    private int protocolType;

    // ———— App信息 ———— //
    private AppInfo appModel;

    // ———— 广告位信息 ———— //
    private SlotInfo slotModel;

    // ———— 设备相关信息 ———— //
    private DeviceInfo deviceModel;
    // ———— 网络相关信息 ———— //
    private NetworkInfo networkModel;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public int getSdktype() {
        return sdktype;
    }

    public void setSdktype(int sdktype) {
        this.sdktype = sdktype;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }


    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }


    public AppInfo getAppModel() {
        return appModel;
    }

    public void setAppModel(AppInfo appModel) {
        this.appModel = appModel;
    }

    public SlotInfo getSlotModel() {
        return slotModel;
    }

    public void setSlotModel(SlotInfo slotModel) {
        this.slotModel = slotModel;
    }

    public DeviceInfo getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(DeviceInfo deviceModel) {
        this.deviceModel = deviceModel;
    }

    public NetworkInfo getNetworkModel() {
        return networkModel;
    }

    public void setNetworkModel(NetworkInfo networkModel) {
        this.networkModel = networkModel;
    }

    public static JSONObject buildRequest2(Context context, AdRequest clientRequest) {
        JSONObject requestModel = new JSONObject();
        try {
            requestModel.put("channelId", clientRequest.getCodeId());
            requestModel.put("apiVersion", AdConfig.getDefault().getSdkVersion());
            requestModel.put("adType", clientRequest.getAdType().getIntValue());
            requestModel.put("appPackage", context.getPackageName());
            requestModel.put("imei",DeviceHelper.getImei(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestModel;
    }


    public static AdRequestParameters buildRequest(Context context, AdRequest clientRequest) {
        AdRequestParameters bean = new AdRequestParameters();

        try {
            if (context != null) {

                if (clientRequest != null) {
                    String channelId = clientRequest.getCodeId();
                    int adType = clientRequest.getAdType().getIntValue();
                    // 请求ID保证唯一
                    bean.setRequestId(channelId + "_" + Long.toString(System.currentTimeMillis()));
                    bean.setChannelId(channelId);
                    bean.setAdType(adType);
                    bean.setSdktype(99);
                    bean.setProtocolType(0);
                }


                // --------------------App相关信息------------------
                AppInfo appModel = new AppInfo();
                appModel.setAppId("");
                appModel.setAppVersion(AppHelper.getVersionName(context));
                appModel.setAppPackage(AppHelper.getAppPackageName(context));
                appModel.setAppName(AppHelper.getAppName(context));
                bean.setAppModel(appModel);

                // --------------------广告位相关信息------------------
                SlotInfo slotModel = new SlotInfo();
                slotModel.setSlotwidth(0);
                slotModel.setSlotheight(0);
                bean.setSlotModel(slotModel);

                // --------------------网络相关信息-----------------
                NetworkInfo networkModel = new NetworkInfo();
                networkModel.setIp(NetworkHelper.getIpAddress(context));
                networkModel.setCellular_id(NetworkHelper.getCid(context));
                networkModel.setConnectionType(NetworkHelper.getNetworkState(context));
                networkModel.setOperatorType(NetworkHelper.getOperators(context));
                double[] data = NetworkHelper.getLongitudeAndLatitude(context);
                networkModel.setLat((float) data[0]);
                networkModel.setLon((float) data[1]);
                bean.setNetworkModel(networkModel);


                // --------------------设备相关信息------------------
                DeviceInfo deviceModel = new DeviceInfo();
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                // 设置androidID
                deviceModel.setAndroidId(androidId);
                // 区分手机平板
                if (DeviceHelper.isPad(context)) {
                    deviceModel.setDeviceType(2);
                } else {
                    deviceModel.setDeviceType(1);
                }
                //安卓设备唯一标识码
                deviceModel.setImei(DeviceHelper.getImei(context));
                //imsi
                deviceModel.setImsi(DeviceHelper.getPhoneImsi(context));
                // 设置MAC地址
                deviceModel.setMac(DeviceHelper.getMacAddress(context));
                //浏览器信息
                deviceModel.setUa(DeviceHelper.getUA(context));
                //手机品牌
                deviceModel.setBrand(Build.BRAND);
                // 设置手机型号
                deviceModel.setModel(Build.MODEL);
                // 设置操作系统
                deviceModel.setOsType(1);

                deviceModel.setReadPhoneState(PermissionsHelper.isGrantReadPhoneStatePermission(context));
                deviceModel.setWriteExtStorage(PermissionsHelper.isGrantWriteExternalStoragePermission(context));

                deviceModel.setOsVersion(DeviceHelper.getOsVersion());
                //设备厂商
                deviceModel.setVendor(Build.MANUFACTURER);
                // 屏幕DPI
                deviceModel.setPpi(UIHelper.getDenstiyDpi(context));
                // 屏幕高度
                deviceModel.setScreenHeight(UIHelper.getScreenHeight(context));
                // 屏幕宽度
                deviceModel.setScreenWidth(UIHelper.getScreenWidth(context));
                //屏幕方向
                deviceModel.setScreenOrientation(UIHelper.getScreenOrientation(context));

                bean.setDeviceModel(deviceModel);
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }

        return bean;
    }


    public JSONObject toJson() {
        AdRequestParameters bean = this;
        JSONObject requestModel = new JSONObject();
        try {
            //App相关信息
            JSONObject appModel = new JSONObject();
            appModel.put("appId","");
            appModel.put("appPackage", bean.getAppModel().getAppPackage());
            appModel.put("appVersion", bean.getAppModel().getAppVersion());
            appModel.put("appName", bean.getAppModel().getAppName());

            //设备相关信息
            JSONObject deviceModel = new JSONObject();
            deviceModel.put("idfa", bean.getDeviceModel().getIdfa());
            deviceModel.put("imei", bean.getDeviceModel().getImei());
            deviceModel.put("mac", bean.getDeviceModel().getMac());
            deviceModel.put("androidId", bean.getDeviceModel().getAndroidId());
            deviceModel.put("model", bean.getDeviceModel().getModel());
            deviceModel.put("vendor", bean.getDeviceModel().getVendor());
            deviceModel.put("screenWidth", bean.getDeviceModel().getScreenWidth());
            deviceModel.put("screenHeight", bean.getDeviceModel().getScreenHeight());
            deviceModel.put("osType", bean.getDeviceModel().getOsType());
            deviceModel.put("osVersion", bean.getDeviceModel().getOsVersion());
            deviceModel.put("deviceType", bean.getDeviceModel().getDeviceType());
            deviceModel.put("ua", bean.getDeviceModel().getUa());
            deviceModel.put("ppi", bean.getDeviceModel().getPpi());
            deviceModel.put("screenOrientation", bean.getDeviceModel().getScreenOrientation());
            deviceModel.put("brand", bean.getDeviceModel().getBrand());
            deviceModel.put("imsi", bean.getDeviceModel().getImsi());
            deviceModel.put("isReadPhoneState", bean.getDeviceModel().isReadPhoneState());
            deviceModel.put("isWriteExtStorage", bean.getDeviceModel().isWriteExtStorage());
            deviceModel.put("battery", DeviceHelper.getCurrentBattery());

            //网络相关信息
            JSONObject networkModel = new JSONObject();
            networkModel.put("ip", bean.getNetworkModel().getIp());
            networkModel.put("connectionType", bean.getNetworkModel().getConnectionType());
            networkModel.put("operatorType", bean.getNetworkModel().getOperatorType());
            networkModel.put("cellular_id", bean.getNetworkModel().getCellular_id());
            networkModel.put("lat", bean.getNetworkModel().getLat());
            networkModel.put("lon", bean.getNetworkModel().getLon());

            //广告位相关信息
            JSONObject slotModel = new JSONObject();
            //slotModel.put("slotId","4777186");
            slotModel.put("slotId","cf96556e3167559ec904ed0fa6871413");
            slotModel.put("slotheight", bean.getSlotModel().getSlotheight());
            slotModel.put("slotwidth", bean.getSlotModel().getSlotwidth());

            //构建广告的请求json格式
            requestModel.put("requestId",bean.getRequestId());
            requestModel.put("channelId",bean.getChannelId());
            requestModel.put("apiVersion", AdConfig.getDefault().getSdkVersion());
            requestModel.put("adType",bean.getAdType());
            requestModel.put("sdktype",bean.getSdktype());
            requestModel.put("protocolType",bean.getProtocolType());
            requestModel.put("device", deviceModel);
            requestModel.put("network", networkModel);
            requestModel.put("slot", slotModel);
            requestModel.put("app",appModel);
            requestModel.put("isRooted",DeviceHelper.isRootedDevice());
            requestModel.put("androidVersion",Build.VERSION.SDK_INT);
            //安装的包信息
//            requestModel.put("installedPkgs", DeviceHelper.getAllUseApps(AdClientContext.getClientContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestModel;

    }


}
