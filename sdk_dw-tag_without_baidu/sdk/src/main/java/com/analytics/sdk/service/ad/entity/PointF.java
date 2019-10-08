package com.analytics.sdk.service.ad.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PointF implements Parcelable {

    private float fx;
    private float fy;
    private int realDownX;
    private int realDownY;
    private int viewWidth;
    private int viewHeight;


    public static PointF obtain(int downX,int downY,int viewWidth,int viewHeight){
        PointF pointF = new PointF();

        float realDownX = ((float)downX / (float)viewWidth);
        float realDownY = ((float)downY / (float)viewHeight);

        pointF.fx = realDownX;
        pointF.fy = realDownY;

        pointF.realDownX = downX;
        pointF.realDownY = downY;

        pointF.viewWidth = viewWidth;
        pointF.viewHeight = viewHeight;

        return pointF;
    }

    public float getFx() {
        return fx;
    }

    public void setFx(float fx) {
        this.fx = fx;
    }

    public float getFy() {
        return fy;
    }

    public void setFy(float fy) {
        this.fy = fy;
    }

    public int getRealDownX() {
        return realDownX;
    }

    public void setRealDownX(int realDownX) {
        this.realDownX = realDownX;
    }

    public int getRealDownY() {
        return realDownY;
    }

    public void setRealDownY(int realDownY) {
        this.realDownY = realDownY;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.fx);
        dest.writeFloat(this.fy);
        dest.writeInt(this.realDownX);
        dest.writeInt(this.realDownY);
        dest.writeInt(this.viewWidth);
        dest.writeInt(this.viewHeight);
    }

    public PointF() {
    }

    protected PointF(Parcel in) {
        this.fx = in.readFloat();
        this.fy = in.readFloat();
        this.realDownX = in.readInt();
        this.realDownY = in.readInt();
        this.viewWidth = in.readInt();
        this.viewHeight = in.readInt();
    }

    public static final Parcelable.Creator<PointF> CREATOR = new Parcelable.Creator<PointF>() {
        @Override
        public PointF createFromParcel(Parcel source) {
            return new PointF(source);
        }

        @Override
        public PointF[] newArray(int size) {
            return new PointF[size];
        }
    };
}
