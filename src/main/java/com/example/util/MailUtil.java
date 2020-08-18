package com.example.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MailUtil {

    private static final String MAIL_TRANSPORT_PROTOCOL = "SMTP";
    private static final String MAIL_SMTP_HOST = "smtp.163.com";

    public static void sendMail(String userName, String password, String toAccount, String title, String content) throws MessagingException, UnsupportedEncodingException {
        //创建连接对象 连接到邮件服务器
        Properties properties = new Properties();
        //设置发送邮件的基本参数
        //发送邮件服务器
        properties.setProperty("mail.transport.protocol", MAIL_TRANSPORT_PROTOCOL);
        properties.put("mail.smtp.host", MAIL_SMTP_HOST);
        //发送端口
        properties.put("mail.smtp.port", "25");
        // 指定验证为true
        properties.setProperty("mail.smtp.auth", "true");
//        properties.setProperty("mail.smtp.timeout","1000");
        //设置发送邮件的账号和密码
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //两个参数分别是发送邮件的账户和密码
                return new PasswordAuthentication(userName,password);
            }
        });

        //创建邮件对象
        Message message = new MimeMessage(session);

        message.addHeader("Content-type", "text/html; charset=UTF-8");
        message.addHeader("format", "flowed");
        message.addHeader("Content-Transfer-Encoding", "8bit");

        //设置发件人
        message.setFrom(new InternetAddress(userName));
//        Address fromAddress = new InternetAddress(MimeUtility.encodeText(Constant.STEWARD_MAIL_NAME, "UTF-8", "B") + " <" + userName + ">");
//        message.setFrom(fromAddress);
        //防止报错554 DT:SPM，抄送给自己
//        message.addRecipient(MimeMessage.RecipientType.CC, fromAddress);
        //设置收件人
        message.setRecipient(Message.RecipientType.TO,new InternetAddress(toAccount));
        //设置主题
        //message.setSubject(title);
        message.setSubject(MimeUtility.encodeText(title, "UTF-8", "B"));
        //设置邮件正文  第二个参数是邮件发送的类型
        message.setContent(content,"text/html;charset=UTF-8");
        //发送一封邮件
        Transport.send(message);
    }

    public static void main(String[] args) {

    }

}
