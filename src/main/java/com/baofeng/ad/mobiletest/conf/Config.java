package com.baofeng.ad.mobiletest.conf;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class Config {

    //执行时间相关配置
    private int executeInterval;
    private int executeTimes;
    private int dailyReportTime;
    private int runTime;

    //抓包相关配置
    private String host;
    private String device;
    private String[] subStrs;
    private int maxErrorCodeNum;
    private int maxErrorCodeOneNum;
    private String location;
    private boolean debug;
    private int maxEmptyNum;

    //每种报告用哪几种通知方式
    private int failNotify;
    private int dailyNotify;
    private boolean emptyNotify;

    //微信配置项
    private int tryTimes;
    private boolean useWX;
    private String corpID;
    private int agentId;
    private String secret;
    private String toParty;

    //邮件配置项
    private boolean useMail;
    private String mailSMTP;
    private String mailUser;
    private String mailPwd;
    private String mailUname;
    private String mailReceiver;
    private String mailCCuser;

    //短信配置项
    private boolean useSMS;
    private String mobiles;
    private String HOST;
    private int PORT;
    private String PATH;
    private String NAME;
    private String PASS;

    //appium相关配置
    private String deviceName;
    private String platformVersion;
    private String appPackage;
    private String appActivity;
    private String url;

    //虚拟机相关配置
    private String STOP_PROCESS_COMMAND;
    private String START_PROCESS_COMMAND;
    private int stopSleepTime;
    private int startSleepTime;

    public Config(String filePath) throws IOException {
        Wini ini = new Wini(new File(filePath));

        //微信配置项
        useWX = ini.get("WX", "useWX", boolean.class);
        if (useWX) {
            tryTimes = ini.get("WX", "tryTimes", int.class);
            corpID = ini.get("WX", "corpID");
            agentId = ini.get("WX", "agentId", int.class);
            secret = ini.get("WX", "secret");
            toParty = ini.get("WX", "toParty");
        }

        //执行时间相关配置
        executeInterval = ini.get("time", "executeInterval", int.class);
        executeTimes = ini.get("time", "executeTimes", int.class);
        dailyReportTime = ini.get("time", "dailyReportTime", int.class);
        runTime = ini.get("time", "runTime", int.class);

        //每种报告用哪几种通知方式
        failNotify = ini.get("notify", "failNotify", int.class);
        dailyNotify = ini.get("notify", "dailyNotify", int.class);
        emptyNotify = ini.get("notify", "emptyNotify", boolean.class);


        //邮件配置项
        useMail = ini.get("mail", "useMail", boolean.class);
        if (useMail) {
            mailSMTP = ini.get("mail", "mailSMTP");
            mailUser = ini.get("mail", "mailUser");
            mailPwd = ini.get("mail", "mailPwd");
            mailUname = ini.get("mail", "mailUname");
            mailReceiver = ini.get("mail", "mailReceiver");
            mailCCuser = ini.get("mail", "mailCCuser");
        }

        //短信配置项
        useSMS = ini.get("SMS", "useSMS", boolean.class);
        if (useSMS) {
            mobiles = ini.get("SMS", "mobiles");
            HOST = ini.get("SMS", "HOST");
            PORT = ini.get("SMS", "PORT", int.class);
            PATH = ini.get("SMS", "PATH");
            NAME = ini.get("SMS", "NAME");
            PASS = ini.get("SMS", "PASS");
        }


        //抓包相关配置
        host = ini.get("capture", "host");
        device = ini.get("capture", "device");
        subStrs = ini.get("capture", "subStrs").split(";");
        maxErrorCodeNum = ini.get("capture", "maxErrorCodeNum", int.class);
        maxErrorCodeOneNum = ini.get("capture", "maxErrorCodeOneNum", int.class);
        location = ini.get("capture", "location");
        debug = ini.get("capture", "debug", boolean.class);
        maxEmptyNum = ini.get("capture", "maxEmptyNum", int.class);

        //appium相关配置
        deviceName = ini.get("section_appium", "deviceName");
        platformVersion = ini.get("section_appium", "platformVersion");
        appPackage = ini.get("section_appium", "appPackage");
        appActivity = ini.get("section_appium", "appActivity");
        url = ini.get("section_appium", "url");

        //VM相关配置
        STOP_PROCESS_COMMAND = ini.get("vm", "STOP_PROCESS_COMMAND");
        START_PROCESS_COMMAND = ini.get("vm", "START_PROCESS_COMMAND");
        stopSleepTime = ini.get("vm", "stopSleepTime", int.class);
        startSleepTime = ini.get("vm", "startSleepTime", int.class);
    }

    public int getTryTimes() {
        return tryTimes;
    }

    public String getCorpID() {
        return corpID;
    }

    public int getAgentId() {
        return agentId;
    }

    public String getSecret() {
        return secret;
    }

    public String getToParty() {
        return toParty;
    }

    public boolean isUseWX() {
        return useWX;
    }

    public int getExecuteInterval() {
        return executeInterval;
    }

    public int getExecuteTimes() {
        return executeTimes;
    }

    public int getDailyReportTime() {
        return dailyReportTime;
    }

    public int getFailNotify() {
        return failNotify;
    }

    public int getDailyNotify() {
        return dailyNotify;
    }

    public boolean isUseMail() {
        return useMail;
    }

    public boolean isUseSMS() {
        return useSMS;
    }

    public String getHost() {
        return host;
    }

    public String getDevice() {
        return device;
    }

    public String[] getSubStrs() {
        return subStrs;
    }

    public int getMaxErrorCodeNum() {
        return maxErrorCodeNum;
    }

    public String getLocation() {
        return location;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }

    public String getUrl() {
        return url;
    }

    public String getMobiles() {
        return mobiles;
    }

    public String getHOST() {
        return HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public String getPATH() {
        return PATH;
    }

    public String getNAME() {
        return NAME;
    }

    public String getPASS() {
        return PASS;
    }

    public String getMailSMTP() {
        return mailSMTP;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getMailPwd() {
        return mailPwd;
    }

    public String getMailUname() {
        return mailUname;
    }

    public String getMailReceiver() {
        return mailReceiver;
    }

    public String getMailCCuser() {
        return mailCCuser;
    }

    public int getRunTime() {
        return runTime;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getMaxErrorCodeOneNum() {
        return maxErrorCodeOneNum;
    }

    public int getMaxEmptyNum() {
        return maxEmptyNum;
    }

    public String getSTOP_PROCESS_COMMAND() {
        return STOP_PROCESS_COMMAND;
    }

    public String getSTART_PROCESS_COMMAND() {
        return START_PROCESS_COMMAND;
    }

    public int getStopSleepTime() {
        return stopSleepTime;
    }

    public int getStartSleepTime() {
        return startSleepTime;
    }

    public boolean isEmptyNotify() {
        return emptyNotify;
    }
}
