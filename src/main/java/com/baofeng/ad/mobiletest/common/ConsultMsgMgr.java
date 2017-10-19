package com.baofeng.ad.mobiletest.common;

import java.util.HashMap;
import java.util.Map;

import static com.zm.frame.log.Log.log;

public class ConsultMsgMgr {
    // key : id(广告位) + xst, value : URLDecoder.decode(raw data);
    private Map<String, String> map = new HashMap<>();

    private static final ConsultMsgMgr instance = new ConsultMsgMgr();

    public static ConsultMsgMgr getInstance() {
        return instance;
    }

    public void clear() {
        map.clear();
    }

    public void putMsg(ConsultBean consultBean) {
        map.put(consultBean.getId() + consultBean.getXst(), consultBean.getRawData());
        //log.info("ConsultMsgMgr putMsg " + consultBean.getId() + consultBean.getXst());
    }

    public String getMsg(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            log.info("找不到 key 为" + key + "的协商");
            return "";
        }
    }

    public Map<String , String> getMap() {
        return map;
    }
}
