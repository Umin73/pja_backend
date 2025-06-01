package com.project.PJA.email.service;

public interface EmailService {
    void sendInvitationEmail(String to, String inviteUrl);
    void sendVerificationCode(String to, String code);
    void sendSignupEmail(String to, String code);
}
