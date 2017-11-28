package com.baofeng.ad.mobiletest.common;

import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.conf.D;
import com.baofeng.ad.mobiletest.conf.Template;
import com.baofeng.ad.mobiletest.msg.body.NotifyMsgBody;
import com.baofeng.ad.mobiletest.notify.NotifyData;
import com.baofeng.ad.mobiletest.server.MyServer;
import com.zm.frame.thread.msg.ThreadMsg;
import com.zm.frame.thread.server.ThreadServer;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.zm.frame.log.Log.log;

public class ReportMsgMgr {
    //存一整天的数据
    //private List<ReportMsg> oneDayMsgs = new ArrayList<>();
    // key : state
    private Map<String, Map<String, Integer>> oneDayErrorcodeMap = new HashMap<>();

    //存每一次运行的数据
    private List<ReportMsg> onceMsgs = new ArrayList<>();

    private static final ReportMsgMgr instance = new ReportMsgMgr();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    //private Lock readLock = rwLock.readLock();
    private Lock writeLock = rwLock.writeLock();

    private ThreadServer server = ThreadServer.getInstance();
    private ConsultMsgMgr consultMsgMgr = ConsultMsgMgr.getInstance();

    private int failNotifyType;
    private int dailyNotifyType;
    private int maxErrorCodeNum;
    private int maxErrorCodeOneNum;
    private int maxEmptyNum;

    //发送日报使用
    private String location;
    private int executeInterval;

    private int emptyNum;
    private boolean restarted;

    public static ReportMsgMgr getInstance() {
        return instance;
    }

    private ReportMsgMgr() {
        Config config = MyServer.getInstance().getConfig();
        failNotifyType = config.getFailNotify();
        dailyNotifyType = config.getDailyNotify();
        maxErrorCodeNum = config.getMaxErrorCodeNum();
        maxErrorCodeOneNum = config.getMaxErrorCodeOneNum();
        location = config.getLocation();
        executeInterval = config.getExecuteInterval();
        maxEmptyNum = config.getMaxEmptyNum();
        emptyNum = 0;
        restarted = false;
    }

    public void pushMsg(String msgJson, String rawData) {
        try{
            ReportMsg msg = ReportMsg.getFromJson(msgJson, rawData);
            writeLock.lock();
            onceMsgs.add(msg);
            writeLock.unlock();
        } catch (Exception e) {
            log.error("msg json转换错误 : " + msgJson + "\r\n" + e);
        }
    }

    //临时发送线程消息, 不可回复
    private void sendToNotifyThread(int threadType, NotifyMsgBody msgBody, int msgType) {
        ThreadMsg msg = new ThreadMsg(-1, -1, -1,
                threadType, -1, -1, msgType, msgBody);
        server.sendThreadMsgTo(msg);
    }

    public void onceAnalyze() {
        Map<String, List<ReportMsg>> errorCodeMap = new HashMap<>();
        NotifyData notifyData = new NotifyData();
        writeLock.lock();
        log.info("分析数据...");

        // 错误errorcode异常
        boolean error = false;// 只打出一个异常
        for(ReportMsg msg : onceMsgs) {
            if (msg.getErrorcode() != null) {
                if ((msg.getErrorcode().equals("2") ||
                        msg.getErrorcode().equals("4") ||
                        msg.getErrorcode().equals("6")) &&
                        !error) {
                    notifyData.addTitle(String.format(Template.HEADLINE_A, msg.getLocation(),
                            msg.getErrorcode(), msg.getId(), msg.getStatus()));
                    notifyData.addContent(String.format(Template.MAIL_A, msg.getRawData(),
                            msg.getItime(), msg.getStatus(),
                            consultMsgMgr.getMsg(msg.getLocation() + msg.getXst())));
                    error = true;// 有一个失败即认为失败
                }
                if (!msg.getErrorcode().trim().equals("3") || !msg.getLocation().trim().equals("wx_tjbanner2")) {
                    if (errorCodeMap.containsKey(msg.getErrorcode())) {
                        errorCodeMap.get(msg.getErrorcode()).add(msg);
                    } else {
                        List<ReportMsg> list = new ArrayList<>();
                        list.add(msg);
                        errorCodeMap.put(msg.getErrorcode(), list);
                    }
                }
                addToOneDayErrorcodeMap(msg.getStatus(), msg.getErrorcode(), 1);
            }
        }

        StringBuffer sb = new StringBuffer("周期内捕获到 " + onceMsgs.size() + " 个报数 : \r\n");
        sb.append("errorcode/个数 : ").append("\r\n");
        for (Map.Entry<String, List<ReportMsg>> entry : errorCodeMap.entrySet()) {
            sb.append(entry.getKey()).append("/").append(entry.getValue().size()).append("\r\n");
        }
        log.info(sb.toString());

        log.info("周期内捕获到协商 : " + consultMsgMgr.getMap().keySet());

        // 指定errorcode的次数大于配置值, 异常
        for (String key : new String[]{"1", "3", "5"}) {
            if(errorCodeMap.containsKey(key)) {
                int tmpMaxNum;
                if (key.equals("1")) {
                    tmpMaxNum = maxErrorCodeOneNum;
                } else {
                    tmpMaxNum = maxErrorCodeNum;
                }
                if (errorCodeMap.get(key).size() >= tmpMaxNum) {
                    List<ReportMsg> msgList = errorCodeMap.get(key);
                    String reason = "errorcode " + key + " 的数量是 " +
                            msgList.size() + ", >=允许的最大值 " + tmpMaxNum;

                    //按照location分别统计个数
                    Map<String, Integer> locationMap = new HashMap<>();
                    for(ReportMsg msg : msgList) {
                        if (locationMap.containsKey(msg.getLocation())) {
                            locationMap.put(msg.getLocation(), locationMap.get(msg.getLocation()) + 1);
                        } else {
                            locationMap.put(msg.getLocation(), 1);
                        }
                    }
                    sb = new StringBuffer("其中按照广告位区分如下: ").append("\r\n");
                    for (Map.Entry<String, Integer> entry: locationMap.entrySet()) {
                        sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\r\n");
                    }

                    notifyData.addTitle(String.format(Template.HEADLINE_B, location, reason));
                    notifyData.addContent(String.format(
                            Template.MAIL_B, location, reason + "\r\n" + sb.toString(),
                            consultMsgMgr.getMsg(msgList.get(0).getLocation() + msgList.get(0).getXst())));
                    break; // 只打出一个异常
                }
            }
        }

        // 抓不到报数异常
        if (onceMsgs.size() == 0) {
            if (++ emptyNum >= maxEmptyNum) {
                if (restarted) {
                    exit();
                }
                log.error("连续" + maxEmptyNum + "次抓不到包, 尝试重启虚拟机");
                notifyData.addTitle("连续" + maxEmptyNum + "次抓不到包, 尝试重启虚拟机");
                notifyData.addContent("连续" + maxEmptyNum + "次抓不到包, 尝试重启虚拟机");
                sendToNotifyThread(D.THREAD_TYPE_APPIUM, null, D.MSG_TYPE_RESTART_VM);
                emptyNum = 0;
                restarted = true;
            } else {
                notifyData.addTitle(String.format(Template.HEADLINE_B, location, "未抓到android盒子报数"));
                notifyData.addContent(String.format(Template.MAIL_B, location, "一次case中没有满足过滤条件的报数", ""));
            }
        } else {
            emptyNum = 0;
            restarted = false;
        }
        // 一次case没有包含0, 1, 2
        /*else if(!errorCodeMap.containsKey("0") && !errorCodeMap.containsKey("1") && !errorCodeMap.containsKey("2")) {
            notifyData.addTitle(String.format(Template.HEADLINE_B, "一屏内的errorcode没有0,1,2"));
            notifyData.addContent(String.format(Template.MAIL_B, "一屏内的errorcode没有0,1,2"));
        }*/

        if (!notifyData.isEmpty()) {
            sendToNotifyThread(D.THREAD_TYPE_NOTIFY, new NotifyMsgBody(failNotifyType, notifyData), D.MSG_TYPE_NOTIFY);
        }
        //oneDayMsgs.addAll(onceMsgs);
        onceMsgs.clear();
        consultMsgMgr.clear();
        writeLock.unlock();
    }

    private void exit() {
        sendToNotifyThread(D.THREAD_TYPE_NOTIFY, new NotifyMsgBody(failNotifyType
                , new NotifyData("重启虚拟机后依然多次无法抓到包, 程序退出"
                , "重启虚拟机后依然多次无法抓到包, 程序退出")), D.MSG_TYPE_NOTIFY);
        sendToNotifyThread(D.THREAD_TYPE_NOTIFY, null, D.MSG_TYPE_SEND_SCREEN_SHOT);
        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    private void addToOneDayErrorcodeMap(String status, String errorcode, int num) {

        //取出某一个status的errorcode的数据
        Map<String, Integer> statusErrorcodeMap;
        if (oneDayErrorcodeMap.containsKey(status)) {
            statusErrorcodeMap = oneDayErrorcodeMap.get(status);
        } else {
            statusErrorcodeMap = new HashMap<>();
        }

        //某个status的errorcode数据处理
        if (statusErrorcodeMap.containsKey(errorcode)) {
            statusErrorcodeMap.replace(errorcode, statusErrorcodeMap.get(errorcode) + num);
        } else {
            statusErrorcodeMap.put(errorcode, num);
        }

        //将数据放回总的status的数据
        oneDayErrorcodeMap.put(status, statusErrorcodeMap);
    }

    private String getOnedayErrorcodeInfo() {
        StringBuffer stringBuffer = new StringBuffer();

        for(Map.Entry<String, Map<String, Integer>> statusEntry : oneDayErrorcodeMap.entrySet()) {
            stringBuffer.append("======================\r\n");
            stringBuffer.append("status : ").append(statusEntry.getKey()).append("\r\n");
            Map<String, Integer> statusErrorCodeMap = statusEntry.getValue();
            for(Map.Entry<String, Integer> entry: statusErrorCodeMap.entrySet()) {
                stringBuffer.append("errorcode \"").append(entry.getKey()).append("\" 的个数为 ")
                        .append(entry.getValue()).append("\r\n");
            }
        }
        return stringBuffer.toString();
    }

    public void dailyAnalyze() {
        writeLock.lock();
        Date date = new Date();

        NotifyData notifyData = new NotifyData(String.format(Template.DAILY_HEADLINE, location, date),
                String.format(Template.DAILY_MAIL, location, date, executeInterval, getOnedayErrorcodeInfo()));

        sendToNotifyThread(D.THREAD_TYPE_NOTIFY, new NotifyMsgBody(dailyNotifyType, notifyData), D.MSG_TYPE_NOTIFY);
        //oneDayMsgs.clear();
        oneDayErrorcodeMap.clear();
        writeLock.unlock();
    }
}
