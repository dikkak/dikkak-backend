package com.dikkak.service;

import com.dikkak.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 메일 전송
    public void sendMail(MailDto dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(dto.getEmailList().toArray(new String[0]));
        message.setSubject(dto.getTitle());
        message.setText(dto.getContent());
        mailSender.send(message);
    }
}
