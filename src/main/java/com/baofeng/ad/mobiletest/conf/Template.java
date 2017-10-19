package com.baofeng.ad.mobiletest.conf;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.zm.frame.log.Log.log;

public class Template {
    private static final String DIR = "notice_template" + File.separator;
    public static String HEADLINE_A;
    public static String HEADLINE_B;
    public static String MAIL_A;
    public static String MAIL_B;
    public static String DAILY_HEADLINE;
    public static String DAILY_MAIL;

    static  {
        try {
            HEADLINE_A = FileUtils.readFileToString(new File(DIR + "HEADLINE_A"), "utf-8");
            HEADLINE_B = FileUtils.readFileToString(new File(DIR + "HEADLINE_B"), "utf-8");
            MAIL_A = FileUtils.readFileToString(new File(DIR + "MAIL_A"), "utf-8");
            MAIL_B = FileUtils.readFileToString(new File(DIR + "MAIL_B"), "utf-8");
            DAILY_HEADLINE = FileUtils.readFileToString(new File(DIR + "DAILY_HEADLINE"), "utf-8");
            DAILY_MAIL = FileUtils.readFileToString(new File(DIR + "DAILY_MAIL"), "utf-8");
        } catch (IOException e) {
            log.error("读取模板文件失败 : " + e.getMessage());
            System.exit(1);
        }
    }
}
