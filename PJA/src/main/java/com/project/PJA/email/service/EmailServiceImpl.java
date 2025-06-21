package com.project.PJA.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendInvitationEmail(String to, String inviteUrl) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("\uD83D\uDCE7 PJA의 워크스페이스 초대 메일입니다.");

            /*String htmlContent = """
    <p>안녕하세요,<br>
    PJA 워크스페이스에 초대드립니다. 🎉</p>
    <br>
    <p>아래 버튼을 클릭하여 워크스페이스에 참여해 주세요.</p>
    <br>
    <p><a href="%s" style="display:inline-block;padding:10px 20px;
    background-color:#FE5000;color:white;text-decoration:none;
    border-radius:5px;">워크스페이스 참여하기</a></p>

    <p>감사합니다. 🙏</p>
    """.formatted(inviteUrl);*/

            String htmlContent = """
    <p style="margin:0 0 16px 0;">안녕하세요,<br>
    PJA 워크스페이스에 초대드립니다. 🎉</p>

    <p style="margin:0 0 16px 0;">아래 버튼을 클릭하여 워크스페이스에 참여해 주세요.</p>

    <p style="margin:0 0 16px 0;">
        <a href="%s" style="display:inline-block;padding:10px 20px;
        background-color:#FE5000;color:white;text-decoration:none;
        border-radius:5px;">워크스페이스 참여하기</a>
    </p>

    <p style="margin:0 0 16px 0;">감사합니다. 🙏</p>
""".formatted(inviteUrl);

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패" + e.getMessage(), e);
        }
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

    @Override
    public void sendFindPwEmail(String to, String certificationNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PJA 비밀번호 찾기 인증번호 메일입니다.");
        message.setText("인증번호는 다음과 같습니다:\n\n" + certificationNumber);

        mailSender.send(message);
    }
}
