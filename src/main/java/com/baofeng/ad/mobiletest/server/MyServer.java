package com.baofeng.ad.mobiletest.server;

import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.conf.D;
import com.zm.frame.thread.server.ThreadServer;
import com.zm.frame.thread.thread.MyThreadGroup;

import java.io.IOException;

import static com.zm.frame.log.Log.log;

public class MyServer {
    private static final MyServer instance = new MyServer();

    private Config config;

    private MyServer() {}

    public void init() {
        // init config
        try {
            config = new Config(D.CONFIGURATION_DIRECTORY_PATH + "conf.properties");
        } catch (IOException e) {
            log.error("读取配置文件失败 : " + e.getMessage());
            System.exit(1);
        }

        // init thread
        new MyClassFactory();

        //通知线程
        new MyThreadGroup(D.THREAD_TYPE_NOTIFY, 1, null);
        //定时线程
        new MyThreadGroup(D.THREAD_TYPE_ON_TIME, 1, 200);
        //appium线程
        new MyThreadGroup(D.THREAD_TYPE_APPIUM, 1, null);
        //处理线程
        new MyThreadGroup(D.THREAD_TYPE_PROCESS, 1, null);


    }

    public static MyServer getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    public void start() {
        init();
        ThreadServer.getInstance().startThreads();
    }
}
