package com.project.PJA.email.service;

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
        message.setTo(to);
        message.setSubject("PJA의 워크스페이스 초대 메일입니다.");
        message.setText("아래 링크를 클릭하여 워크스페이스에 참여하세요:\n\n" + inviteUrl);

        mailSender.send(message);
    }

    @Override
    public void sendVerificationCode(String to, String certificationNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PJA 인증번호 메일입니다.");
        message.setText("인증번호는 다음과 같습니다:\n\n" + certificationNumber);

        mailSender.send(message);
    }

    @Override
    public void sendSignupEmail(String to, String certificationNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PJA 회원가입 인증번호 메일입니다.");
        message.setText("인증번호는 다음과 같습니다:\n\n" + certificationNumber);

        mailSender.send(message);
    }
}
