package com.baofeng.ad.mobiletest.notify;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

import static com.zm.frame.log.Log.log;

public class MailNotify implements Notify {

    private Message message;
    private Session session;
    private Transport transport;

    private EmailConfig config;

    public MailNotify(String smtp, String user, String pwd, String uname, String receiver, String ccuser) {
        config= new EmailConfig(smtp, user, pwd, uname, receiver, ccuser);
    }

    @Override
    public void init() {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", config.getSmtp());
        props.put("mail.smtp.auth","true");
        session = Session.getDefaultInstance(props,new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(config.getUname(),config.getPwd());
            }
        });
        //session.setDebug(true);
        message = new MimeMessage(session);
        try {
            InternetAddress from = new InternetAddress(config.getUser());
            message.setFrom(from);
            if(null != config.getReceiver() && !config.getReceiver().isEmpty()) {
                InternetAddress[] internetAddressTo = new InternetAddress().parse(config.getReceiver());
                message.setRecipients(Message.RecipientType.TO, internetAddressTo);
            }
            if(null != config.getCcuser() && !config.getCcuser().isEmpty()) {
                InternetAddress[] internetAddressCC = new InternetAddress().parse(config.getCcuser());
                message.setRecipients(Message.RecipientType.CC, internetAddressCC);
            }
            transport = session.getTransport("smtp");
        } catch (Exception e) {
            log.error("初始化邮件失败 : " + e);
        }
    }

    @Override
    public void send(NotifyData notifyData) {
        int tryTimes = 3;
        while((-- tryTimes) >= 0) {
            try {
                transport.connect(config.getSmtp(),config.getUser(),config.getPwd());
                message.setSubject(notifyData.getTitle());
                String content = notifyData.getContent();
                message.setContent(content,"text/plain;charset=GBK");
                message.saveChanges();
                transport.sendMessage(message,message.getAllRecipients());
                transport.close();
            } catch (MessagingException e) {
                log.error("发送邮件失败 : " + e);
                continue;
            }
            log.info("发送邮件成功 : \r\n" +
                    notifyData.getTitle() + "\r\n" +
                    notifyData.getContent());
            return;
        }
        log.error("发送邮件尝试多次均失败");
    }
}

class EmailConfig {
    private String smtp;
    private String user;
    private String pwd;
    private String uname;
    private String receiver;
    private String ccuser;

    public String getSmtp() {
        return smtp;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUname() {
        return uname;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getCcuser() {
        return ccuser;
    }

    public EmailConfig(String smtp, String user, String pwd, String uname, String receiver, String ccuser){
        this.smtp = smtp;
        this.user = user;
        this.pwd = pwd;
        this.uname = uname;
        this.receiver = receiver;
        this.ccuser = ccuser;
    }
}