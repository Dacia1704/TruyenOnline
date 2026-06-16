package com.dacia1704.truyenonline.module.story.controller;

import com.dacia1704.truyenonline.module.story.dto.request.StoryCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryFilter;
import com.dacia1704.truyenonline.module.story.dto.request.StoryPublishRequestReviewRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import com.dacia1704.truyenonline.module.story.service.StoryService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryController {

    StoryService storyService;

    @GetMapping("/")
    public ApiResponse<PageResponse<StoryResponse>> getStories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam() String search,
            @RequestParam() StoryType type,
            @RequestParam() StoryStatus status,
            @RequestParam() boolean isPublished
            ) {
        PageResponse<StoryResponse> result = storyService.getStories(page, size,search, StoryFilter.builder()
                .isPublished(isPublished)
                .status(status)
                .type(type).build());
        return ApiResponse.success(result);
    }

    @GetMapping("/{slug}")
    public ApiResponse<StoryResponse> getStoriesBySlug(@PathVariable("slug") String slug) {
        StoryResponse result = storyService.getStoryBySlug(slug);
        return ApiResponse.success(result);
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('story:create')")
    public ApiResponse<StoryResponse> createStory(@RequestBody @Valid StoryCreateRequest request) {
        StoryResponse result = storyService.createStory(request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('story:update_own', 'story:update_any')")
    public ApiResponse<StoryResponse> updateStory(@PathVariable("id") String id, @RequestBody @Valid StoryUpdateRequest request) {
        StoryResponse result = storyService.updateStory(id,request);
        return ApiResponse.success(result);
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
            @RequestParam() String storyId,
            @RequestParam() StoryPublishRequestStatus status,
            @RequestParam() String uploaderId)
    {
        var result = storyService.getPublishRequests(page,size, storyId, status,uploaderId);
        return ApiResponse.success(result);
    }
    @GetMapping("/publish-requests/me")
    @PreAuthorize("hasAnyAuthority('story:update_own')")
    public ApiResponse<PageResponse<StoryPublishRequestResponse>> getMyPublishRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam() String storyId,
            @RequestParam() StoryPublishRequestStatus status)
    {
        var result = storyService.getMyPublishRequests(page,size, storyId, status);
        return ApiResponse.success(result);
    }
    @PatchMapping("/publish-requests/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryPublishRequestResponse> approvePublishRequest(@PathVariable("id") String id, @RequestBody @Valid StoryPublishRequestReviewRequest request) {
        var result = storyService.approvePublishRequest(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/publish-requests/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoryPublishRequestResponse> rejectPublishRequest(@PathVariable("id") String id, @RequestBody @Valid StoryPublishRequestReviewRequest request) {
        var result = storyService.rejectPublishRequest(id, request);
        return ApiResponse.success(result);
    }



}
