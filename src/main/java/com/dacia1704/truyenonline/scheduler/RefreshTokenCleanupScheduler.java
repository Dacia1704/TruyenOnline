package com.dacia1704.truyenonline.scheduler;

import com.dacia1704.truyenonline.module.authentication.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "${scheduler.refresh-token.cron}") // 3h sáng mỗi ngày
    public void cleanupExpiredRefreshTokens() {

        log.info("Start cleanup expired refresh tokens");

        long deleted = refreshTokenService.deleteExpiredTokens();

        log.info("Deleted {} refresh tokens", deleted);
    }

}