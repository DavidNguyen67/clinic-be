package com.camel.clinic.service;

import com.camel.clinic.service.auth.AuthServiceInv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final String KEY = "registered:emails";
    private static final int BATCH_SIZE = 1000;
    private final StringRedisTemplate redisTemplate;
    private final AuthServiceInv authServiceInv;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Long size = redisTemplate.opsForSet().size(KEY);

        // Chỉ warm-up khi cache thực sự trống
        if (size != null && size > 0) {
            log.info("[EmailCache] Cache already has {} emails, skipping warm-up", size);
            return;
        }

        log.info("[EmailCache] Cache empty, warming up in batches...");
        warmUpInBatches();
    }

    private void warmUpInBatches() {
        int page = 0;
        int total = 0;

        while (true) {
            // Query từng batch, không load hết 1 lần
            List<String> batch = authServiceInv.findEmailsBatch(
                    PageRequest.of(page, BATCH_SIZE)
            );

            if (batch.isEmpty()) break;

            redisTemplate.opsForSet().add(KEY, batch.toArray(new String[0]));
            total += batch.size();
            page++;

            log.info("[EmailCache] Warmed up {} emails so far...", total);
        }

        log.info("[EmailCache] Warm-up complete. Total: {}", total);
    }
}