package com.baofeng.ad.mobiletest.notify;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import java.net.URLEncoder;

import static com.zm.frame.log.Log.log;

public class SMSNotify implements Notify {

    private String HOST;
    private int PORT;
    /*private String PATH;
    private String NAME;
    private String PASS;
    private String mobiles;*/

    private String URL;
    private HttpClient httpClient;

    public SMSNotify(String mobiles, String HOST, int PORT, String PATH, String NAME, String PASS) {
        this.HOST = HOST;
        this.PORT = PORT;
        URL = PATH + "?name=" + NAME + "&pass=" +
                PASS + "&mobiles=" + mobiles + "&content=";
    }

    @Override
    public void init() {
        httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(HOST, PORT, "http");
    }

    @Override
    public void send(NotifyData notifyData) {
        log.debug("短信通知 : \r\n" +
                notifyData.getTitle());
        GetMethod method = null;
        try {
            method = new GetMethod(URL + URLEncoder.encode(notifyData.getTitle(), "utf-8"));
            httpClient.executeMethod(method);
            String[] responses = method.getResponseBodyAsString().split(",");
            if (responses.length == 2) {
                if (responses[1].trim().equals("00")) {
                    log.info("发送SMS成功 : " + notifyData.getTitle());
                } else {
                    log.error("发送SMS失败, 错误代码 : " + responses[1]);
                }
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }
}
