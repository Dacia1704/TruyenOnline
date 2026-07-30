package com.dacia1704.truyenonline.scheduler;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaCleanupScheduler {

    private final MediaFileService mediaFileService;
    private final AuditLogService auditLogService;

    @Scheduled(cron = "${scheduler.media.cron}")
    public void cleanupMedia() throws IOException {
        log.info("Start cleanup orphan image file");

        mediaFileService.deleteOrphanImageFileBeforeDays();
        auditLogService.log(AuditAction.DELETE, AuditObjectType.SYSTEM, null, null, null, "Clean up orpahn image file");
        log.info("Deleted orpha image file");
    }

}