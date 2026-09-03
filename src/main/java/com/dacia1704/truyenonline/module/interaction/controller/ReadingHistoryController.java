package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryFilter;
import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryFilterType;
import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-histories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryController {
    ReadingHistoryService readingHistoryService;

    @GetMapping("/story/{storyId}")
    public ApiResponse<ReadingHistoryResponse> getLastReadingChapterInStory(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @PathVariable("storyId") String storyId) {
        ReadingHistoryResponse result =
                readingHistoryService.getLastReadingChapterInStory(sessionId, storyId);
        return ApiResponse.success(result);
    }

    @GetMapping("")
    public ApiResponse<PageResponse<ReadingHistoryResponse>> getMyReadingHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReadingHistoryFilterType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate toDate,
            @RequestHeader(value = "Session-Id", required = false) String sessionId) {
        ReadingHistoryFilter filter =
                ReadingHistoryFilter.builder()
                        .type(type)
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .sessionId(sessionId)
                        .build();
        PageResponse<ReadingHistoryResponse> result =
                readingHistoryService.getMyReadingHistory(page, size, filter);
        return ApiResponse.success(result);
    }

    @PostMapping("")
    public ApiResponse<ReadingHistoryResponse> createMyReadingHistory(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @RequestBody @Valid ReadingHistoryRequest request) {
        ReadingHistoryResponse result =
                readingHistoryService.updateMyReadingHistory(sessionId, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("")
    public ApiResponse<String> deleteMyReadingHistory(
            @RequestHeader(value = "Session-Id", required = false) String sessionId) {
        readingHistoryService.deleteMyReadingHistory(sessionId);
        return ApiResponse.success("Xóa lịch sử đọc truyện thành công");
    }
}
