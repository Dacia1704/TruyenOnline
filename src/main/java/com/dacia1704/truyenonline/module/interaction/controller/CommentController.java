package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.request.CommentCreateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUpdateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.interaction.service.CommentService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("")
    public ApiResponse<PageResponse<CommentResponse>> getCommentChapter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam() String chapterId) {
        PageResponse<CommentResponse> result =
                commentService.getCommentChapter(chapterId, page, size);
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
}
