package com.dacia1704.truyenonline.module.administration.controller;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterCreateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModerationActionController {
    ModerationActionService moderationActionService;

    @GetMapping("/moderation-actions")
    public ApiResponse<PageResponse<ModerationActionResponse>> getChaptersBySlugStory(
            @RequestBody @Valid ModerationActionPageRequest request) {
        PageResponse<ModerationActionResponse> result = moderationActionService.getModerationActions(request);
        return ApiResponse.success(result);
    }
}
