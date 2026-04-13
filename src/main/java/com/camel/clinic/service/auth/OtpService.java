package com.camel.clinic.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final long OTP_TTL_MINUTES = 10;
    private static final String OTP_PREFIX = "otp:";
    private static final String RESET_TOKEN_PREFIX = "reset_token:";

    private final SecureRandom random = new SecureRandom();
    private final StringRedisTemplate redisTemplate;

    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        String key = OTP_PREFIX + normalize(email);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public String verifyOtpAndIssueResetToken(String email, String otp) {
        String key = OTP_PREFIX + normalize(email);
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            return null;
        }
        redisTemplate.delete(key);

        String resetToken = UUID.randomUUID().toString();
        String tokenKey = RESET_TOKEN_PREFIX + resetToken;
        redisTemplate.opsForValue().set(tokenKey, normalize(email), OTP_TTL_MINUTES, TimeUnit.MINUTES);
        return resetToken;
    }

    public String consumeResetToken(String resetToken) {
        String tokenKey = RESET_TOKEN_PREFIX + resetToken;
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            return null;
        }
        redisTemplate.delete(tokenKey);
        return email;
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}