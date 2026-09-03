package com.dacia1704.truyenonline.module.story.controller;

import com.dacia1704.truyenonline.module.story.dto.request.AuthorCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.AuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryAuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.AuthorResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.service.AuthorService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {

    AuthorService authorService;

    @GetMapping
    public ApiResponse<PageResponse<AuthorResponse>> getAuthors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ApiResponse.success(authorService.getAuthors(page, size, search));
    }

    @GetMapping("/{slug}")
    public ApiResponse<AuthorResponse> getAuthorBySlug(@PathVariable String slug) {
        return ApiResponse.success(authorService.getAuthorBySlug(slug));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('author:create')")
    public ApiResponse<AuthorResponse> createAuthor(
            @ModelAttribute @Valid AuthorCreateRequest request) throws IOException {
        return ApiResponse.success(authorService.createAuthor(request));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('author:update')")
    public ApiResponse<AuthorResponse> updateAuthor(
            @PathVariable String id, @ModelAttribute @Valid AuthorUpdateRequest request)
            throws IOException {
        return ApiResponse.success(authorService.updateAuthor(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('author:delete')")
    public ApiResponse<String> deleteAuthor(@PathVariable String id) {
        authorService.deleteAuthor(id);
        return ApiResponse.success("Xóa tác giả thành công");
    }

    // API Cập nhật danh sách tác giả cho Story (Ghi đè)
    @PutMapping("/story/{storyId}")
    @PreAuthorize("hasAnyAuthority('story:update_own', 'story:update_any')")
    public ApiResponse<StoryResponse> updateStoryAuthors(
            @PathVariable String storyId,
            @RequestBody @Valid List<StoryAuthorUpdateRequest> requests) {
        StoryResponse result = authorService.updateStoryAuthors(storyId, requests);
        return ApiResponse.success(result);
    }
}
