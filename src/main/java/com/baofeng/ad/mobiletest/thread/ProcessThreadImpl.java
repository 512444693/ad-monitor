package com.baofeng.ad.mobiletest.thread;

import com.baofeng.ad.mobiletest.capture.Capture;
import com.baofeng.ad.mobiletest.common.ReportMsgMgr;
import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.server.MyServer;
import com.zm.frame.thread.msg.ThreadMsg;
import com.zm.frame.thread.thread.BlockingThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zm.frame.log.Log.log;
import static com.baofeng.ad.mobiletest.conf.D.*;

public class ProcessThreadImpl extends BlockingThread {

    private Capture capture;
    private ReportMsgMgr reportMsgMgr = ReportMsgMgr.getInstance();

    private static final ExecutorService es = Executors.newFixedThreadPool(1);

    public ProcessThreadImpl(int threadType, int threadId) {
        super(threadType, threadId);
    }

    @Override
    protected void init() {
        Config config = MyServer.getInstance().getConfig();
        capture = new Capture(config.getDevice(),
                config.getHost(), config.getSubStrs(), config.getLocation());
        capture.init();
    }

    @Override
    protected void threadProcessMsg(ThreadMsg msg) {
        switch (msg.msgType) {
            case MSG_TYPE_CAPTURE_START :
                log.info("抓包开始");
                es.execute(new CaptureTask());
                break;
            case MSG_TYPE_CAPTURE_STOP :
                capture.stop();
                log.info("抓包结束");
                //结束后分析数据
                reportMsgMgr.onceAnalyze();
                break;
            case MSG_TYPE_DAILY_REPORT :
                log.info("处理日报");
                reportMsgMgr.dailyAnalyze();
                break;
            case MSG_TYPE_EXIT :
                log.info("运行结束, 等待分析数据, 发送邮件");
                reportMsgMgr.dailyAnalyze();
                try {
                    //等待发邮件结束
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    log.error(e);
                }
                System.exit(0);
                break;
            default:
                super.threadProcessMsg(msg);
        }
    }

    class CaptureTask implements Runnable {

        @Override
        public void run() {
            capture.start();
        }
    }

}
