package com.baofeng.ad.mobiletest.common;

import java.util.HashMap;
import java.util.Map;

import static com.zm.frame.log.Log.log;

public class ConsultMsgMgr {
    // key : id(广告位) + xst
    private Map<String, ConsultBean> map = new HashMap<>();

    private static final ConsultMsgMgr instance = new ConsultMsgMgr();

    public static ConsultMsgMgr getInstance() {
        return instance;
    }

    public void clear() {
        map.clear();
    }

    public void putMsg(ConsultBean consultBean) {
        map.put(consultBean.getId() + consultBean.getXst(), consultBean);
        //log.info("ConsultMsgMgr putMsg " + consultBean.getId() + consultBean.getXst());
    }

    public ConsultBean getMsg(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            log.info("找不到 key 为" + key + "的协商");
            return new ConsultBean("", "", "", Long.MAX_VALUE);
        }
    }

    public Map<String , ConsultBean> getMap() {
        return map;
    }
}
