package com.dacia1704.truyenonline.scheduler;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
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
    private final AuditLogService auditLogService;

    @Scheduled(cron = "${scheduler.refresh-token.cron}") // 3h sáng mỗi ngày
    public void cleanupExpiredRefreshTokens() {

        log.info("Start cleanup expired refresh tokens");

        long deleted = refreshTokenService.deleteExpiredTokens();
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.SYSTEM,
                null,
                null,
                null,
                " Cleanup expired refresh tokens");

        log.info("Deleted {} refresh tokens", deleted);
    }
}
