package com.analytics.sdk.service.ad.entity;

import java.io.Serializable;

public class NetworkInfo implements Serializable {
    private static final long serialVersionUID = -3168716509065793151L;
    private String ip;
    private int connectionType;
    private int operatorType;
    private String cellular_id;
    private float lat;
    private float lon;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(int connectionType) {
        this.connectionType = connectionType;
    }

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    public String getCellular_id() {
        return cellular_id;
    }

    public void setCellular_id(String cellular_id) {
        this.cellular_id = cellular_id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
