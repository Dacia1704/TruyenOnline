package com.dacia1704.truyenonline.module.interaction.controller;

import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading-histories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryController {
    ReadingHistoryService readingHistoryService;

    @GetMapping("")
    public ApiResponse<PageResponse<ReadingHistoryResponse>> getMyReadingHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Session-Id", required = false) String sessionId) {
        PageResponse<ReadingHistoryResponse> result =
                readingHistoryService.getMyReadingHistory(page, size, sessionId);

        return ApiResponse.success(result);
    }

    @PostMapping("")
    public ApiResponse<ReadingHistoryResponse> createMyReadingHistory(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @RequestBody @Valid ReadingHistoryRequest request) {
        ReadingHistoryResponse result =
                readingHistoryService.createMyReadingHistory(sessionId, request);
        return ApiResponse.success(result);
    }

    @PatchMapping("")
    public ApiResponse<ReadingHistoryResponse> updateMyReadingHistory(
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
