package com.baofeng.ad.mobiletest.server;

import com.baofeng.ad.mobiletest.thread.AppiumThreadImpl;
import com.baofeng.ad.mobiletest.thread.NotifyThreadImpl;
import com.baofeng.ad.mobiletest.thread.OnTimeThreadImpl;
import com.baofeng.ad.mobiletest.thread.ProcessThreadImpl;
import com.zm.frame.thread.server.ClassFactory;
import com.zm.frame.thread.task.Task;
import com.zm.frame.thread.thread.BasicThread;

import static com.baofeng.ad.mobiletest.conf.D.*;

public class MyClassFactory extends ClassFactory {
    @Override
    public BasicThread genThread(int threadType, int threadId, Object arg) {
        BasicThread ret = null;

        switch (threadType) {
            case THREAD_TYPE_ON_TIME :
                ret = new OnTimeThreadImpl(threadType, threadId, arg);
                break;

            case THREAD_TYPE_NOTIFY :
                ret = new NotifyThreadImpl(threadType, threadId);
                break;

            case THREAD_TYPE_APPIUM :
                ret = new AppiumThreadImpl(threadType, threadId);
                break;

            case THREAD_TYPE_PROCESS :
                ret = new ProcessThreadImpl(threadType, threadId);
                break;
        }

        return ret;
    }

    @Override
    public Task genTask(int taskType, int taskId, BasicThread thread, int time, Object arg) {
        return null;
    }
}
