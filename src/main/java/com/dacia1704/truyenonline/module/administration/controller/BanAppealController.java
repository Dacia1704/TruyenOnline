package com.dacia1704.truyenonline.module.administration.controller;

import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealApproveRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealRejectRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealResponse;
import com.dacia1704.truyenonline.module.administration.service.BanAppealService;
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
@RequestMapping("/ban-appeals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BanAppealController {

    BanAppealService banAppealService;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<BanAppealResponse> create(@ModelAttribute @Valid BanAppealCreateRequest request) {
        return ApiResponse.success(banAppealService.create(request));
    }

    /**
     * User xem các khiếu nại của mình
     */
    @GetMapping("/me")
    public ApiResponse<PageResponse<BanAppealResponse>> getMe(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.success(
                banAppealService.getMe(page, size)
        );
    }

    /**
     * Owner xóa đơn khiếu nại
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(
            @PathVariable String id
    ) {

        banAppealService.delete(id);

        return ApiResponse.success("Xóa khiếu nại thành công");
    }

    /**
     * Admin xem toàn bộ khiếu nại
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<BanAppealResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.success(
                banAppealService.getAll(page, size)
        );
    }

    /**
     * Admin chấp nhận khiếu nại
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BanAppealResponse> approve(
            @PathVariable String id,
            @RequestBody @Valid BanAppealApproveRequest request
    ) {

        return ApiResponse.success(
                banAppealService.approve(id, request)
        );
    }

    /**
     * Admin từ chối khiếu nại
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BanAppealResponse> reject(
            @PathVariable String id,
            @RequestBody @Valid BanAppealRejectRequest request
    ) {

        return ApiResponse.success(
                banAppealService.reject(id, request)
        );
    }
}