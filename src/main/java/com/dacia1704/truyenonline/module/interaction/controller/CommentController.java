package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.request.CommentBanRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentCreateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUnbanRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUpdateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.interaction.service.CommentService;
import com.dacia1704.truyenonline.module.story.dto.request.StoryBanRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUnbanRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("/chapter/{id}")
    public ApiResponse<PageResponse<CommentResponse>> getCommentChapter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("id") String chapterId) {
        PageResponse<CommentResponse> result =
                commentService.getCommentChapter(chapterId, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/story/{id}")
    public ApiResponse<PageResponse<CommentResponse>> getCommentStory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("id") String storyId) {
        PageResponse<CommentResponse> result =
                commentService.getCommentStory(storyId, page, size);
        return ApiResponse.success(result);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('comment:create')")
    public ApiResponse<CommentResponse> createMyComment(
            @RequestBody @Valid CommentCreateRequest request) {
        CommentResponse result = commentService.createMyComment(request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('comment:create')")
    public ApiResponse<CommentResponse> updateMyComment(
            @PathVariable("id") String id, @RequestBody @Valid CommentUpdateRequest request) {
        CommentResponse result = commentService.updateMyComment(id, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('comment:delete_own')")
    public ApiResponse<String> deleteMyComment(@PathVariable("id") String id) {
        commentService.deleteMyComment(id);
        return ApiResponse.success("Xóa bình luận thành công");
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasAnyRole('ADMIN', 'UPLOADER')")
    public ApiResponse<CommentResponse> banComment(
            @PathVariable("id") String id, @RequestBody @Valid CommentBanRequest request) throws IOException {
        CommentResponse result = commentService.banComment(id, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/unban")
    @PreAuthorize("hasAnyRole('ADMIN', 'UPLOADER')")
    public ApiResponse<CommentResponse> unbanComment(
            @PathVariable("id") String id, @RequestBody @Valid CommentUnbanRequest request) throws IOException {
        CommentResponse result = commentService.unbanComment(id, request);
        return ApiResponse.success(result);
    }
}
