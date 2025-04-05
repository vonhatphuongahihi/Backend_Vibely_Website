package com.example.vibely_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("Vibely <" + fromEmail + ">");
        
        message.setTo(to);
        message.setSubject("Mã xác thực đặt lại mật khẩu");
        message.setText("Mã xác thực của bạn là: " + code + ". Mã này sẽ hết hạn sau 10 phút.");
        mailSender.send(message);
    }
}

