package com.camel.clinic.service.presence;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class PresenceServiceImp implements PresenceService {

    private static final String KEY_PREFIX = "presence:";
    private static final Duration TTL = Duration.ofSeconds(60);

    private final StringRedisTemplate redis;

    @Override
    public void setOnline(String userId) {
        redis.opsForValue().set(KEY_PREFIX + userId, "online", TTL);
    }

    @Override
    public void setOffline(String userId) {
        redis.delete(KEY_PREFIX + userId);
    }

    @Override
    public boolean isOnline(String userId) {
        return Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + userId));
    }
}