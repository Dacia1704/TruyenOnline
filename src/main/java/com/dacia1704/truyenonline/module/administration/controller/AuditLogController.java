package com.dacia1704.truyenonline.module.administration.controller;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.AuditLogResponse;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogController {
    AuditLogService auditLogService;

    @GetMapping("/audit-logs")
    public ApiResponse<PageResponse<AuditLogResponse>> getChaptersBySlugStory(
            @RequestBody @Valid AuditLogPageRequest request) {
        PageResponse<AuditLogResponse> result = auditLogService.getAuditLogs(request);
        return ApiResponse.success(result);
    }
}
