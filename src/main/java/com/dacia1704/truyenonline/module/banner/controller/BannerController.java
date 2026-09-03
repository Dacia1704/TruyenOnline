package com.dacia1704.truyenonline.module.banner.controller;

import com.dacia1704.truyenonline.module.banner.dto.request.BannerCreateRequest;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerStatusRequest;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerUpdateRequest;
import com.dacia1704.truyenonline.module.banner.dto.response.BannerResponse;
import com.dacia1704.truyenonline.module.banner.service.BannerService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerController {

    BannerService bannerService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> create(@ModelAttribute @Valid BannerCreateRequest request) {
        return ApiResponse.success(bannerService.createBanner(request));
    }

    @PutMapping(
            value = "/{id}",
            consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> update(
            @PathVariable String id, @ModelAttribute @Valid BannerUpdateRequest request) {
        return ApiResponse.success(bannerService.updateBanner(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> delete(@PathVariable String id) {
        bannerService.deleteBanner(id);
        return ApiResponse.success("Xóa banner thành công");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> updateStatus(
            @PathVariable String id, @RequestBody @Valid BannerStatusRequest request) {
        return ApiResponse.success(bannerService.updateStatus(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<BannerResponse> getById(@PathVariable String id) {
        return ApiResponse.success(bannerService.getById(id));
    }

    /** Public API */
    @GetMapping("/active")
    public ApiResponse<java.util.List<BannerResponse>> getAllActive() {
        return ApiResponse.success(bannerService.getAllActive());
    }

    /** Admin API */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<BannerResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam boolean isActive) {
        return ApiResponse.success(bannerService.getAll(page, size, isActive));
    }
}
