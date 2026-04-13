package com.camel.clinic.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
public class TokenStoreService {

    private static final String REFRESH_PREFIX = "RT:";
    private static final String BLACKLIST_PREFIX = "BL:";

    private final StringRedisTemplate redis;
    private final Duration refreshExpiration;

    public TokenStoreService(
            StringRedisTemplate redis,
            @Value("${jwt.refresh-expiration:2592000000}") long refreshExpirationMs) {
        this.redis = redis;
        this.refreshExpiration = Duration.ofMillis(refreshExpirationMs);
    }

    // ── Refresh token ────────────────────────────────────────────────────────

    public void storeRefreshTokenHash(String userId, String refreshToken) {
        redis.opsForValue().set(refreshKey(userId), sha256(refreshToken), refreshExpiration);
    }

    public boolean matchesRefreshToken(String userId, String refreshToken) {
        String stored = redis.opsForValue().get(refreshKey(userId));
        return stored != null && stored.equals(sha256(refreshToken));
    }

    public void deleteRefreshToken(String userId) {
        redis.delete(refreshKey(userId));
    }

    // ── JTI blacklist ────────────────────────────────────────────────────────

    /**
     * @param jti JWT ID cần blacklist
     * @param ttl Thời gian còn lại của access token — để Redis tự xóa sau khi token hết hạn
     */
    public void blacklistJti(String jti, Duration ttl) {
        redis.opsForValue().set(blacklistKey(jti), "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(blacklistKey(jti)));
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private String refreshKey(String userId) {
        return REFRESH_PREFIX + userId;
    }

    private String blacklistKey(String jti) {
        return BLACKLIST_PREFIX + jti;
    }

    private String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}