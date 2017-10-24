package com.baofeng.ad.mobiletest.conf;

import com.zm.frame.conf.Definition;

public class D extends Definition {

    // notify type
    public static final int NOTIFY_TYPE_WX = 1;
    public static final int NOTIFY_TYPE_MAIL = 2;
    public static final int NOTIFY_TYPE_SMS = 4;

    //thread type
    public static final int THREAD_TYPE_ON_TIME = 1001;
    public static final int THREAD_TYPE_NOTIFY = 1002;
    public static final int THREAD_TYPE_APPIUM = 1003;
    public static final int THREAD_TYPE_PROCESS = 1004;

    //message type
    public static final int MSG_TYPE_NOTIFY = 2001;
    public static final int MSG_TYPE_APPIUM_START = 2002;
    public static final int MSG_TYPE_CAPTURE_START = 2003;
    public static final int MSG_TYPE_CAPTURE_STOP = 2004;
    public static final int MSG_TYPE_DAILY_REPORT = 2005;
    public static final int MSG_TYPE_EXIT = 2006;
    public static final int MSG_TYPE_SEND_SCREEN_SHOT = 2007;
    public static final int MSG_TYPE_RESTART_VM = 2008;
}
