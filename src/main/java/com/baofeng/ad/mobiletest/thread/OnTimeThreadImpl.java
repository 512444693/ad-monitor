package com.baofeng.ad.mobiletest.thread;

import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.server.MyServer;
import com.zm.frame.thread.thread.NoBlockingThread;

import static com.baofeng.ad.mobiletest.conf.D.*;
import static com.zm.frame.log.Log.log;

import java.util.Calendar;

public class OnTimeThreadImpl extends NoBlockingThread {

    private int executeInterval;
    private int executeTimes;
    private int dailyReportTime;
    private int runTime;

    private long lastExecuteTime = 0;
    private long processedTime = 0;
    private long START_TIME = System.currentTimeMillis();

    public OnTimeThreadImpl(int threadType, int threadId, Object arg) {
        super(threadType, threadId, (int) arg);
    }

    @Override
    protected void init() {
        Config config = MyServer.getInstance().getConfig();
        executeInterval = config.getExecuteInterval();
        executeTimes = config.getExecuteTimes();
        dailyReportTime = config.getDailyReportTime();
        runTime = config.getRunTime();
    }

    @Override
    protected void afterProcessMsg() {
        long timeNow = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        if ((timeNow - lastExecuteTime) >= executeInterval * 60 * 1000) {
            for (int i = 0; i < executeTimes; i++) {
                //通知appium线程执行
                sendThreadMsgTo(MSG_TYPE_APPIUM_START, null, THREAD_TYPE_APPIUM);
            }
            lastExecuteTime = timeNow;
        }

        if(calendar.get(Calendar.HOUR_OF_DAY) == dailyReportTime &&
                calendar.get(Calendar.MINUTE) == 0 &&
                calendar.get(Calendar.SECOND) == 0 &&
                !processed(calendar.getTimeInMillis() / 1000)) {
            sendThreadMsgTo(MSG_TYPE_DAILY_REPORT, null, THREAD_TYPE_PROCESS);
        }

        if (runTime > 0) {
            if ((timeNow - START_TIME) >= runTime * 60 * 1000) {
                sendThreadMsgTo(MSG_TYPE_EXIT, null, THREAD_TYPE_PROCESS);
                START_TIME = timeNow;
            }
        }

    }

    private boolean processed(long timeNowInSecond) {
        if (processedTime == timeNowInSecond) {
            return true;
        } else {
            processedTime = timeNowInSecond;
            return false;
        }
    }
}
