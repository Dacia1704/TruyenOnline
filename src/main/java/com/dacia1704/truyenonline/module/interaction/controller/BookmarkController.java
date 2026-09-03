package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.response.BookmarkResponse;
import com.dacia1704.truyenonline.module.interaction.service.BookmarkService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkController {

    BookmarkService bookmarkService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public Page<BookmarkResponse> getMyBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return bookmarkService.getMyBookmarks(page, size);
    }

    @GetMapping("/{storyId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<BookmarkResponse> getBookmarkByStoryId(@PathVariable String storyId) {

        BookmarkResponse result = bookmarkService.getBookmarkByStoryId(storyId);

        return ApiResponse.success(result);
    }

    @PostMapping("/{storyId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<BookmarkResponse> createMyBookmark(@PathVariable String storyId) {

        BookmarkResponse result = bookmarkService.createBookmark(storyId);

        return ApiResponse.success(result);
    }

    @DeleteMapping("/{storyId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> deleteBookmark(@PathVariable String storyId) {

        bookmarkService.deleteBookmark(storyId);

        return ApiResponse.success("Xóa theo dõi truyện thành công");
    }
}
