package com.camel.clinic.service.auth;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory OTP store (email -> otp with expiry) and reset token store.
 *
 * NOTE: For production, replace with Redis.
 */
@Service
public class OtpService {
    private static final long OTP_TTL_MS = 10 * 60 * 1000; // 10 minutes

    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpValue> otpStore = new ConcurrentHashMap<>();
    private final Map<String, ResetTokenValue> resetTokenStore = new ConcurrentHashMap<>();

    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpStore.put(normalize(email), new OtpValue(otp, System.currentTimeMillis() + OTP_TTL_MS));
        return otp;
    }

    public String verifyOtpAndIssueResetToken(String email, String otp) {
        String key = normalize(email);
        OtpValue v = otpStore.get(key);
        if (v == null || v.isExpired() || !v.otp().equals(otp)) {
            return null;
        }
        otpStore.remove(key);

        String resetToken = UUID.randomUUID().toString();
        resetTokenStore.put(resetToken, new ResetTokenValue(key, System.currentTimeMillis() + OTP_TTL_MS));
        return resetToken;
    }

    public String consumeResetToken(String resetToken) {
        ResetTokenValue v = resetTokenStore.get(resetToken);
        if (v == null || v.isExpired()) {
            resetTokenStore.remove(resetToken);
            return null;
        }
        resetTokenStore.remove(resetToken);
        return v.email();
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private record OtpValue(String otp, long expiresAtMs) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtMs;
        }
    }

    private record ResetTokenValue(String email, long expiresAtMs) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtMs;
        }
    }
}

