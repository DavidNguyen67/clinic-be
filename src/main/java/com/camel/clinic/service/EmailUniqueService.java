package com.camel.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailUniqueService {

    private static final String KEY = "registered:emails";
    private final StringRedisTemplate redisTemplate;

    public boolean existsInCache(String email) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(KEY, email));
    }

    public void addToCache(String email) {
        redisTemplate.opsForSet().add(KEY, email);
    }

    public void removeFromCache(String email) {
        redisTemplate.opsForSet().remove(KEY, email);
    }
}