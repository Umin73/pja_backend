package com.project.PJA.workspace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendInvitationEmail(String to, String inviteUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo();
        message.setSubject("PJA의 워크스페이스 초대 메일입니다.");
        message.setText("아래 링크를 클릭하여 워크스페이스에 참여하세요:\n\n" + inviteUrl);

        mailSender.send(message);
    }
}
