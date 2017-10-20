package com.baofeng.ad.mobiletest.thread;

import com.baofeng.ad.mobiletest.conf.Config;
import com.baofeng.ad.mobiletest.msg.body.NotifyMsgBody;
import com.baofeng.ad.mobiletest.notify.*;
import com.baofeng.ad.mobiletest.server.MyServer;
import com.zm.frame.thread.msg.ThreadMsg;
import com.zm.frame.thread.thread.BlockingThread;

import static com.baofeng.ad.mobiletest.conf.D.*;

public class NotifyThreadImpl extends BlockingThread {

    private Notify wxNotify;
    private Notify mailNotify;
    private Notify smsNotify;

    public NotifyThreadImpl(int threadType, int threadId) {
        super(threadType, threadId);
    }

    @Override
    protected void init() {
        Config config = MyServer.getInstance().getConfig();
        if (config.isUseWX()) {
            wxNotify = new WXNotify(config.getCorpID(), config.getSecret(),
                    config.getToParty(), config.getAgentId(), config.getTryTimes());
            wxNotify.init();
        } else {
            wxNotify = new NullNotify();
        }

        if (config.isUseMail()) {
            mailNotify = new MailNotify(config.getMailSMTP(), config.getMailUser(),
                    config.getMailPwd(), config.getMailUname(), config.getMailReceiver(),
                    config.getMailCCuser());
            mailNotify.init();
        } else {
            mailNotify = new NullNotify();
        }

        if (config.isUseSMS()) {
            smsNotify = new SMSNotify(config.getMobiles(), config.getHOST(),
                    config.getPORT(), config.getPATH(), config.getNAME(), config.getPASS());
            smsNotify.init();
        } else {
            smsNotify = new NullNotify();
        }
    }

    @Override
    protected void threadProcessMsg(ThreadMsg msg) {
        switch(msg.msgType) {
            case MSG_TYPE_NOTIFY :
                NotifyMsgBody body = (NotifyMsgBody) msg.msgBody;

                if((body.getNotifyType() & NOTIFY_TYPE_MAIL) == NOTIFY_TYPE_MAIL) {
                    mailNotify.send(body.getNotifyData());
                }

                if((body.getNotifyType() & NOTIFY_TYPE_WX) == NOTIFY_TYPE_WX) {
                    wxNotify.send(body.getNotifyData());
                }

                if((body.getNotifyType() & NOTIFY_TYPE_SMS) == NOTIFY_TYPE_SMS) {
                    smsNotify.send(body.getNotifyData());
                }

                break;
            default:
                super.threadProcessMsg(msg);
        }

    }

}
