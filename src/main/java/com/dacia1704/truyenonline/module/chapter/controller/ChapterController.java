package com.dacia1704.truyenonline.module.chapter.controller;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterCreateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterUpdateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.service.ChapterService;
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
public class ChapterController {
    ChapterService chapterService;

    @GetMapping("/stories/{slug}/chapters")
    public ApiResponse<PageResponse<ChapterResponse>> getChaptersBySlugStory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("slug") String slug,
            @RequestParam() String search
    ) {
        PageResponse<ChapterResponse> result = chapterService.getChaptersBySlug(page, size,slug,search);
        return ApiResponse.success(result);
    }

    @GetMapping("/chapters/{id}")
    public ApiResponse<ChapterResponse> getChapters(@PathVariable("id") String id) {
        ChapterResponse result = chapterService.getChapterById(id);
        return ApiResponse.success(result);
    }

    @PostMapping("/stories/{id}/chapters")
    public ApiResponse<ChapterResponse> createChapters(@PathVariable("id") String id, @RequestBody @Valid ChapterCreateRequest request) {
        ChapterResponse result = chapterService.createChapter(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/chapters/{id}")
    public ApiResponse<ChapterResponse> updateChapter(@PathVariable("id") String id, @RequestBody @Valid ChapterUpdateRequest request) {
        ChapterResponse result = chapterService.updateChapters(id, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/api/chapter{id}")
    public ApiResponse<String> deleteChapter(@PathVariable("id") String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.success("Xóa chương thành công");
    }
}
