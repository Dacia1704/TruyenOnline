package com.dacia1704.truyenonline.module.chapter.controller;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterPageListRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterPageResponse;
import com.dacia1704.truyenonline.module.chapter.service.ChapterPageService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterPageController {
    ChapterPageService chapterPageService;

    @GetMapping("/{id}/pages")
    public ApiResponse<List<ChapterPageResponse>> getChapterPages(@PathVariable("id") String id) {
        List<ChapterPageResponse> result = chapterPageService.getChapterPagesByChapterId(id);
        return ApiResponse.success(result);
    }

    @PostMapping(value = "/pages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<ChapterPageResponse>> createChapterPages(
            @ModelAttribute @Valid ChapterPageListRequest request) throws IOException {
        List<ChapterPageResponse> result = chapterPageService.createChapterPage(request);
        return ApiResponse.success(result);
    }

    @PatchMapping(value = "/pages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<ChapterPageResponse>> updateChapterPages(
            @ModelAttribute @Valid ChapterPageListRequest request) throws IOException {
        List<ChapterPageResponse> result = chapterPageService.updateChapterPage(request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}/page")
    public ApiResponse<String> deleteChapter(@PathVariable("id") String id) throws IOException {
        chapterPageService.deleteChapterPageByChapter(id);
        return ApiResponse.success("Xóa thành công!");
    }
}
