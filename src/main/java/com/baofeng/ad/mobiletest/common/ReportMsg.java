package com.baofeng.ad.mobiletest.common;

import com.google.gson.Gson;

public class ReportMsg {
    private static final Gson gson = new Gson();

    private String id;
    private String status;
    private String location;
    private String errorcode;
    private String itime;
    private String xst;

    private String rawData;

    private ReportMsg() {}

    public static ReportMsg getFromJson(String json, String rawData) {
        return gson.fromJson(json, ReportMsg.class).setRawData(rawData);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getItime() {
        return itime;
    }

    public void setItime(String itime) {
        this.itime = itime;
    }

    public String getXst() {
        return xst;
    }

    public void setXst(String xst) {
        this.xst = xst;
    }

    public ReportMsg setRawData(String json) {
        this.rawData = json;
        return this;
    }

    public String getRawData() {
        return rawData;
    }

    @Override
    public String toString() {
        return "ReportMsg{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", errorcode='" + errorcode + '\'' +
                ", itime='" + itime + '\'' +
                ", rawData='" + rawData + '\'' +
                '}';
    }
}
