package com.dacia1704.truyenonline.module.story.controller;

import com.dacia1704.truyenonline.module.story.dto.request.*;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import com.dacia1704.truyenonline.module.story.service.StoryService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryController {

    StoryService storyService;

    @PostMapping("/list")
    public ApiResponse<PageResponse<StoryResponse>> getStories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody @Valid StoryFilter filter) {
        PageResponse<StoryResponse> result = storyService.getStories(page, size, filter);
        return ApiResponse.success(result);
    }

    @PostMapping("/list/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<StoryResponse>> getStoriesByAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody @Valid StoryAdminFilter filter) {
        PageResponse<StoryResponse> result = storyService.getStoriesByAdmin(page, size, filter);
        return ApiResponse.success(result);
    }

    @GetMapping("/slug/{slug}")
    public ApiResponse<StoryResponse> getStoriesBySlug(@PathVariable("slug") String slug) {
        StoryResponse result = storyService.getStoryBySlug(slug);
        return ApiResponse.success(result);
    }

    @GetMapping("/id/{id}")
    public ApiResponse<StoryResponse> getStoriesById(@PathVariable("id") String id) {
        StoryResponse result = storyService.getStoryById(id);
        return ApiResponse.success(result);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('story:create')")
    public ApiResponse<StoryResponse> createStory(
            @ModelAttribute @Valid StoryCreateRequest request) {
        StoryResponse result = storyService.createStory(request);
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/publish-requests")
    @PreAuthorize("hasAuthority('story:create')")
    public ApiResponse<StoryPublishRequestResponse> requestPublish(
            @PathVariable("id") String id,
            @RequestBody @Valid StoryPublishRequestCreateRequest request) {
        StoryPublishRequestResponse result = storyService.requestPublish(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('story:update_own', 'story:update_any')")
    public ApiResponse<StoryResponse> updateStory(
            @PathVariable("id") String id, @RequestBody @Valid StoryUpdateRequest request) {
        StoryResponse result = storyService.updateStory(id, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/publish-requests/{id}")
    @PreAuthorize("hasAnyAuthority('story:delete_own', 'story:delete_any')")
    public ApiResponse<String> deletePublishRequest(@PathVariable("id") String id) {
        storyService.deletePublishRequest(id);
        return ApiResponse.success("Xóa yêu cầu xuất bản thành công");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('story:delete_own', 'story:delete_any')")
    public ApiResponse<String> deleteStory(@PathVariable("id") String id) {
        storyService.deleteStory(id);
        return ApiResponse.success("Xóa thành công");
    }

    @GetMapping("/publish-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<StoryPublishRequestResponse>> getPublishRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String storyId,
            @RequestParam(required = false) StoryPublishRequestStatus status,
            @RequestParam(required = false) String uploaderId) {
        var result = storyService.getPublishRequests(page, size, storyId, status, uploaderId);
        return ApiResponse.success(result);
    }

    @GetMapping("/publish-requests/me")
    @PreAuthorize("hasAnyAuthority('story:update_own')")
    public ApiResponse<PageResponse<StoryPublishRequestResponse>> getMyPublishRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String storyId,
            @RequestParam(required = false) StoryPublishRequestStatus status) {
        var result = storyService.getMyPublishRequests(page, size, storyId, status);
        return ApiResponse.success(result);
    }

    @PatchMapping("/publish-requests/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryPublishRequestResponse> approvePublishRequest(
            @PathVariable("id") String id,
            @RequestBody @Valid StoryPublishRequestReviewRequest request) {
        var result = storyService.approvePublishRequest(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/publish-requests/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryPublishRequestResponse> rejectPublishRequest(
            @PathVariable("id") String id,
            @RequestBody @Valid StoryPublishRequestReviewRequest request) {
        var result = storyService.rejectPublishRequest(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryResponse> banStory(
            @PathVariable("id") String id, @RequestBody @Valid StoryBanRequest request) {
        StoryResponse result = storyService.banStory(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryResponse> unbanStory(
            @PathVariable("id") String id, @RequestBody @Valid StoryUnbanRequest request) {
        StoryResponse result = storyService.unbanStory(id, request);
        return ApiResponse.success(result);
    }
}
