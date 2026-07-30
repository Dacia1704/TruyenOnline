package com.dacia1704.truyenonline.module.chapter.controller;

import com.dacia1704.truyenonline.module.chapter.dto.request.*;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.service.ChapterService;
import com.dacia1704.truyenonline.module.user.dto.request.UserBanRequest;
import com.dacia1704.truyenonline.module.user.dto.request.UserUnbanRequest;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
            @RequestParam() String search) {
        PageResponse<ChapterResponse> result = chapterService.getChaptersBySlug(page, size, slug, search);
        return ApiResponse.success(result);
    }

    @GetMapping("/chapters/{id}")
    public ApiResponse<ChapterResponse> getChapters(@PathVariable("id") String id) {
        ChapterResponse result = chapterService.getChapterById(id);
        return ApiResponse.success(result);
    }

    @PostMapping("/stories/{id}/chapters")
    @PreAuthorize("hasAuthority('chapter:create')")
    public ApiResponse<ChapterResponse> createChapters(
            @PathVariable("id") String id, @RequestBody @Valid ChapterCreateRequest request) throws IOException {
        ChapterResponse result = chapterService.createChapter(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/chapters/{id}/update-content")
    @PreAuthorize("hasAuthority('chapter:update_own')")
    public ApiResponse<ChapterResponse> updateChapterContent(
            @PathVariable("id") String id, @RequestBody @Valid ChapterContentUpdateRequest request) {
        ChapterResponse result = chapterService.updateChapterContent(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/chapters/update-publish-status")
    @PreAuthorize("hasAuthority('chapter:update_own')")
    public ApiResponse<String> updateChapterPublishStatus(
            @RequestBody @Valid ChapterUpdatePublishStatusRequest request) {
        chapterService.updatePublishStatusChapters(request);
        return ApiResponse.success("Update trạng thái xuất bản thành công");
    }

    @PatchMapping("/chapters/{id}")
    @PreAuthorize("hasAuthority('chapter:update_own')")
    public ApiResponse<ChapterResponse> updateChapter(
            @PathVariable("id") String id, @RequestBody @Valid ChapterUpdateRequest request) {
        ChapterResponse result = chapterService.updateChapter(id, request);
        return ApiResponse.success(result);
    }


    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ChapterResponse> banChapter(
            @PathVariable("id") String id, @RequestBody @Valid ChapterBanRequest request) throws IOException {
        ChapterResponse result = chapterService.banChapter(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ChapterResponse> unbanChapter(
            @PathVariable("id") String id, @RequestBody @Valid ChapterUnbanRequest request) throws IOException {
        ChapterResponse result = chapterService.unbanChapter(id, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/chapter/{id}")
    @PreAuthorize("hasAuthority('chapter:delete_own')")
    public ApiResponse<String> deleteChapter(@PathVariable("id") String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.success("Xóa chương thành công");
    }
}
