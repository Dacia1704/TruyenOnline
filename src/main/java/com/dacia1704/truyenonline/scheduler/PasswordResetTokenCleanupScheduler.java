package com.dacia1704.truyenonline.scheduler;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.authentication.repository.PasswordResetTokenRepository;
import com.dacia1704.truyenonline.module.authentication.service.PasswordResetTokenService;
import com.dacia1704.truyenonline.module.authentication.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenCleanupScheduler {

    private final PasswordResetTokenService passwordResetTokenService;
    private final AuditLogService auditLogService;

    @Scheduled(cron = "${scheduler.password-reset-token.cron}") // 3h sáng mỗi ngày
    public void cleanupNotUsableRefreshTokens() {

        log.info("Start cleanup not usable tokens");

        long deleted = passwordResetTokenService.deleteNotUsablePasswordResetToken();
        auditLogService.log(AuditAction.DELETE, AuditObjectType.SYSTEM, null, null, null, "Cleanup not usable tokens");


        log.info("Deleted {} not usable tokens", deleted);
    }

}