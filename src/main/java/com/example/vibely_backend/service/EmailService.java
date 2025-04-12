package com.example.vibely_backend.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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

    public void sendInquiryResponseEmail(String to, String response, String username) {
        try {
            if(to == null || to.isEmpty()) {
                throw new IllegalArgumentException("Email không được để trống");
            }
    
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "Vibely");
            helper.setTo(to);
            helper.setSubject("Phản hồi từ Vibely");
        
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #222;">
                    <h2 style="color: #086280;">Xin chào %s!</h2>
                    <p>Cảm ơn bạn đã liên hệ với <strong>Vibely</strong>. Chúng tôi đã nhận được thắc mắc của bạn và dưới đây là phản hồi từ đội ngũ hỗ trợ:</p>
                    
                    <div style="background: #f4f4f4; padding: 10px 15px; border-left: 4px solid #086280; margin: 10px 0;">
                        <p style="margin: 0;"><strong>Phản hồi từ đội ngũ hỗ trợ:</strong></p>
                        <p style="margin: 0;">%s</p>
                    </div>
    
                    <p>Nếu bạn cần thêm hỗ trợ, đừng ngần ngại liên hệ lại với chúng tôi.</p>
    
                    <p>Trân trọng,</p>
                    <p><strong>Đội ngũ Vibely</strong></p>
                </div>
            """.formatted(username != null ? username : "bạn", response);
    
            helper.setText(htmlContent, true);
    
            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage(), e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi mã hóa email: " + e.getMessage(), e);
        }
    }
}

