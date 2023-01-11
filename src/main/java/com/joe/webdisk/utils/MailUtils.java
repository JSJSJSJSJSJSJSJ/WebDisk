package com.joe.webdisk.utils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailUtils {

    //从配置文件中注入发件人的姓名
    @Value("${spring.mail.username}")
    private String fromEmail;

    //邮件发送器
    private JavaMailSenderImpl mailSender;
    Logger logger = LogUtils.getInstance(MailUtils.class);

    public MailUtils(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public String sendCode(String email, String userName, String password) {
        int code = (int) ((Math.random()*9+1)*100000);
        logger.info("开始发送繁杂邮件...");
        logger.info("mailSender对象为："+mailSender);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        try {
            messageHelper.setSubject("网盘-邮箱验证");
            messageHelper.setText("<h2 >网盘-简洁、优雅、免费</h2>" +
                    "<h3>用户注册-邮箱验证<h3/>" +
                    "您现在正在注册网盘账号<br>" +
                    "验证码: <span style='color : red'>"+code+"</span><br>" +
                    "用户名 :"+userName+
                    "<br>密码 :"+password+
                    "<hr>"+
                    "<h5 style='color : red'>如果并非本人操作,请忽略本邮件</h5>",true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        logger.info("mailSender对象为："+mimeMessage);
        mailSender.send(mimeMessage);
        return String.valueOf(code);
    }
}
