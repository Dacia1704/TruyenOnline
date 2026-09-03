package com.dacia1704.truyenonline.module.administration.controller;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModerationActionController {
    ModerationActionService moderationActionService;

    @PostMapping("/moderation-actions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<ModerationActionResponse>> getModerationActions(
            @RequestBody @Valid ModerationActionPageRequest request) {
        PageResponse<ModerationActionResponse> result =
                moderationActionService.getModerationActions(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/moderation-actions/{id}")
    @PreAuthorize("hasRole('UPLOADER')")
    public ApiResponse<ModerationActionResponse> getModerationActionsMe(
            @PathVariable("id") String id) {
        ModerationActionResponse result = moderationActionService.getModrationAction(id);
        return ApiResponse.success(result);
    }
}
