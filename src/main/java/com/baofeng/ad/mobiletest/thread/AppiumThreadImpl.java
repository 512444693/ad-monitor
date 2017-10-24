package com.baofeng.ad.mobiletest.thread;

import com.baofeng.ad.mobiletest.appium.StorminAppium;
import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.server.MyServer;
import com.baofeng.ad.mobiletest.utils.VmUtils;
import com.zm.frame.thread.msg.ThreadMsg;
import com.zm.frame.thread.thread.BlockingThread;

import static com.baofeng.ad.mobiletest.conf.D.*;
import static com.zm.frame.log.Log.log;

public class AppiumThreadImpl extends BlockingThread {
    private StorminAppium appium;
    private boolean debug;

    public AppiumThreadImpl(int threadType, int threadId) {
        super(threadType, threadId);
    }

    @Override
    protected void init() {
        Config config = MyServer.getInstance().getConfig();
        appium = new StorminAppium(config.getDeviceName(), config.getPlatformVersion(),
                config.getAppPackage(), config.getAppActivity(), config.getUrl());
        debug = config.isDebug();
    }

    @Override
    protected void threadProcessMsg(ThreadMsg msg) {
        switch (msg.msgType) {
            case MSG_TYPE_APPIUM_START:
                log.info("Appium 开始执行");

                //执行前先通知处理线程开始抓包
                sendThreadMsgTo(MSG_TYPE_CAPTURE_START, null, THREAD_TYPE_PROCESS);
                //给一点时间等待抓包开始
                sleep(5);

                //防止异常, 使其在try块中运行
                try {
                    if (debug) {
                        Thread.sleep(20000);
                    } else {
                        if (!appium.StartStorm()) {
                            log.error("调用Appium失败!!!");
                        }
                    }
                } catch (Exception e) {
                    log.error("Appium调用失败, 异常 : " + e.getMessage());
                }

                log.info("Appium 执行结束");
                //执行结束后通知处理线程停止抓包
                sendThreadMsgTo(MSG_TYPE_CAPTURE_STOP, null, THREAD_TYPE_PROCESS);
                //给一点时间等待抓包结束
                sleep(5);
                break;
            case MSG_TYPE_RESTART_VM :
                VmUtils.restartProcess();
                break;
            default:
                super.threadProcessMsg(msg);
        }
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
        }
    }
}
