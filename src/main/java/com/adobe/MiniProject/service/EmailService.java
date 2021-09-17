package com.adobe.MiniProject.service;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

public class EmailService {
    public static JavaMailSender getMailSender() {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        
        ((JavaMailSenderImpl) mailSender).setHost("smtp.gmail.com");
        ((JavaMailSenderImpl) mailSender).setPort(25);
          
        ((JavaMailSenderImpl) mailSender).setUsername("roombook98@gmail.com");
        ((JavaMailSenderImpl) mailSender).setPassword("adobe@miniprj123");
          
        Properties props = ((JavaMailSenderImpl) mailSender).getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }
    
    public static SimpleMailMessage sendMail(String toEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("roombook98@gmail.com");
        return mailMessage;
    }
}
