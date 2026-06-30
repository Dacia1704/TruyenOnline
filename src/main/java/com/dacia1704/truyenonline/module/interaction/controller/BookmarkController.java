package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.response.BookmarkResponse;
import com.dacia1704.truyenonline.module.interaction.service.BookmarkService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkController {
    BookmarkService bookmarkService;

    @GetMapping("")
    public ApiResponse<BookmarkResponse> getBookmarkByStoryId(@RequestParam() String storyId) {
        BookmarkResponse result = bookmarkService.getBookmarkByStoryId(storyId);
        return ApiResponse.success(result);
    }

    @PostMapping("")
    public ApiResponse<BookmarkResponse> createMyReadingHistory(@RequestParam() String storyId) {
        BookmarkResponse result = bookmarkService.createBookmark(storyId);
        return ApiResponse.success(result);
    }

    @DeleteMapping("")
    public ApiResponse<String> deleteMyReadingHistory(@RequestParam() String storyId) {
        bookmarkService.deleteBookmark(storyId);
        return ApiResponse.success("Xóa đánh dấu truyện thành công");
    }
}
