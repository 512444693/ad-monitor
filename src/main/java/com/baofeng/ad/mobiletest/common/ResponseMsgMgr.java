package com.baofeng.ad.mobiletest.common;

import java.util.ArrayList;
import java.util.Iterator;

public class ResponseMsgMgr {

    // time : rawData
    private ArrayList<ResponseBean> list = new ArrayList<ResponseBean>();

    private static final ResponseMsgMgr instance = new ResponseMsgMgr();

    public static ResponseMsgMgr getInstance() {
        return instance;
    }

    public void clear() {
        list.clear();
    }

    public void pushMsg(ResponseBean bean) {
        list.add(bean);
    }

    public String getLaterResp(long time) {
        Iterator<ResponseBean> iterator = list.iterator();
        while(iterator.hasNext()) {
            ResponseBean bean = iterator.next();
            if (bean.getTime() > time) {
                return bean.getRawData();
            }
        }
        return "";
    }
}
