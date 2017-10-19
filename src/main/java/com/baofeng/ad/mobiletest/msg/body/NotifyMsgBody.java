package com.baofeng.ad.mobiletest.msg.body;

import com.baofeng.ad.mobiletest.notify.NotifyData;
import com.zm.frame.thread.msg.ThreadMsgBody;

public class NotifyMsgBody extends ThreadMsgBody {
    private int notifyType;

    private NotifyData notifyData;

    public NotifyMsgBody(int notifyType, NotifyData notifyData) {
        this.notifyType = notifyType;
        this.notifyData = notifyData;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public NotifyData getNotifyData() {
        return notifyData;
    }
}
