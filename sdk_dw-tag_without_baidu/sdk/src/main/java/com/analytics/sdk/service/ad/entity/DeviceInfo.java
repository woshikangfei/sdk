package com.analytics.sdk.service.ad.entity;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 6283256940651995848L;
    private String idfa;
    private String imei;
    private String mac;
    private String androidId;
    private String model;
    private String vendor;
    private String osVersion;
    private String ua;
    private String brand;
    private String imsi;
    private int ppi;
    private int screenOrientation;
    private int screenWidth;
    private int screenHeight;
    private int osType;
    private int deviceType;
    private boolean readPhoneState;
    private boolean writeExtStorage;

    public boolean isReadPhoneState() {
        return readPhoneState;
    }

    public void setReadPhoneState(boolean readPhoneState) {
        this.readPhoneState = readPhoneState;
    }

    public boolean isWriteExtStorage() {
        return writeExtStorage;
    }

    public void setWriteExtStorage(boolean writeExtStorage) {
        this.writeExtStorage = writeExtStorage;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public int getPpi() {
        return ppi;
    }

    public void setPpi(int ppi) {
        this.ppi = ppi;
    }

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(int screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
