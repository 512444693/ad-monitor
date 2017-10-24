package com.baofeng.ad.mobiletest.utils;

import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.server.MyServer;

import java.io.IOException;

import static com.zm.frame.log.Log.log;

public class VmUtils {
    private static String STOP_PROCESS_COMMAND;
    private static String START_PROCESS_COMMAND;
    private static int stopSleepTime;
    private static int startSleepTime;

    static {
        Config config = MyServer.getInstance().getConfig();
        STOP_PROCESS_COMMAND = config.getSTOP_PROCESS_COMMAND();
        START_PROCESS_COMMAND = config.getSTART_PROCESS_COMMAND();
        stopSleepTime = config.getStopSleepTime();
        startSleepTime = config.getStartSleepTime();
    }

    public static void restartProcess() {
        log.info("关闭虚拟机");
        exec(STOP_PROCESS_COMMAND, stopSleepTime);
        log.info("启动虚拟机");
        exec(START_PROCESS_COMMAND, startSleepTime);
    }

    private static void exec(String command, int sleepSeconds) {
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(sleepSeconds * 1000);
        } catch (IOException e) {
            log.error("执行命令异常 :" + e );
        } catch (InterruptedException e) {
            log.error(e);
        }
    }
}
