package com.dacia1704.truyenonline.scheduler;

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

    @Scheduled(cron = "${scheduler.password-reset-token.cron}") // 3h sáng mỗi ngày
    public void cleanupNotUsableRefreshTokens() {

        log.info("Start cleanup cleanup not usable tokens");

        long deleted = passwordResetTokenService.deleteNotUsablePasswordResetToken();

        log.info("Deleted {} not usable tokens", deleted);
    }

}