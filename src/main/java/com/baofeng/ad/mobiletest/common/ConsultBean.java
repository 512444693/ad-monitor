package com.baofeng.ad.mobiletest.common;

// id对应着上报协议中的广告位置
public class ConsultBean {
    private String id;
    private String xst;
    private String rawData;

    public ConsultBean() {}


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
}
