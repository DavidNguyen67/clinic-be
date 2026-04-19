package com.camel.clinic.service.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlotLockService {

    private static final String LOCK_PREFIX = "SLOT:";
    private static final long LOCK_TTL_SECONDS = 300;

    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(UUID doctorId, LocalDate date, LocalTime time, UUID requestId) {
        String key = LOCK_PREFIX + doctorId + ":" + date + ":" + time;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                key,
                requestId.toString(),
                Duration.ofSeconds(LOCK_TTL_SECONDS)
        ));
    }

    public void releaseLock(UUID doctorId, LocalDate date, LocalTime time) {
        String key = LOCK_PREFIX + doctorId + ":" + date + ":" + time;
        redisTemplate.delete(key);
    }
}

