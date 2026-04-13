package com.camel.clinic.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Minimal email service.
 * <p>
 * Current implementation logs OTP to server log.
 * Replace with JavaMailSender (spring-boot-starter-mail) for real email.
 */
@Service
@Slf4j
public class EmailService {

    public void sendOtpEmail(String email, String otp) {
//        TODO: Implement real email sending using JavaMailSender
        log.info("[DEV-EMAIL] Send OTP to {}: {}", email, otp);
    }
}

