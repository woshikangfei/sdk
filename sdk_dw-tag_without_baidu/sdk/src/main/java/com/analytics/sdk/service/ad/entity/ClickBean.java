package com.analytics.sdk.service.ad.entity;

/**
 * Created by yangminghui on 2018/5/29.
 */

public class ClickBean {

    private DataBean data;
    private int ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public static class DataBean {

        private String clickid;
        private String dstlink;

        public String getClickid() {
            return clickid;
        }

        public void setClickid(String clickid) {
            this.clickid = clickid;
        }

        public String getDstlink() {
            return dstlink;
        }

        public void setDstlink(String dstlink) {
            this.dstlink = dstlink;
        }
    }
}
