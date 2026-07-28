package com.dacia1704.truyenonline.module.payment.controller;

import com.dacia1704.truyenonline.module.payment.dto.request.SubscriptionPlanRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionPlanResponse;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.service.SubscriptionPlanService;
import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.service.GenreService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plan")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPlanController {
    SubscriptionPlanService subscriptionPlanService;

    @GetMapping("")
    public ApiResponse<List<SubscriptionPlanResponse>> getSubscriptionPlans(@RequestParam(defaultValue = "true") Boolean isActive) {
        List<SubscriptionPlanResponse> result = subscriptionPlanService.getPlans(isActive);
        return ApiResponse.success(result);
    }
    @GetMapping("/{code}")
    public ApiResponse<SubscriptionPlanResponse> getSubscriptionPlan(@PathVariable("code") String code) {
        SubscriptionPlanResponse result = subscriptionPlanService.getPlan(code);
        return ApiResponse.success(result);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SubscriptionPlanResponse> createSubscriptionPlan(@RequestBody @Valid SubscriptionPlanRequest request) {
        SubscriptionPlanResponse result = subscriptionPlanService.createPlan(request);
        return ApiResponse.success(result);
    }

    @PatchMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SubscriptionPlanResponse> updateGenre(
            @PathVariable("code") String code, @RequestBody @Valid SubscriptionPlanRequest request) {
        SubscriptionPlanResponse result = subscriptionPlanService.updatePlan(code, request);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteGenre(@PathVariable("code") String code) {
        subscriptionPlanService.deletePlan(code);
        return ApiResponse.success("Xóa gói thành công");
    }
}
