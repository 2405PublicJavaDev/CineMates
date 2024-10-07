package com.filmfellows.cinemates.domain.emailverification.model.service;

public interface EmailVerificationService {
    void sendPasswordResetEmail(String toEmail, String resetLink);
    boolean isValidToken(String token);
    String verifyToken(String token);
    String generateResetToken(String memberId);
}