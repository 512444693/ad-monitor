package com.baofeng.ad.mobiletest.common;

// id对应着上报协议中的广告位置
public class ConsultBean {
    private String id;
    private String xst;
    private String rawData;
    private long time;

    public ConsultBean() {}

    public ConsultBean(String id, String xst, String rawData, long time) {
        this.id = id;
        this.xst = xst;
        this.rawData = rawData;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXst() {
        return xst;
    }

    public void setXst(String xst) {
        this.xst = xst;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
