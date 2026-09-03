package com.dacia1704.truyenonline.module.payment.controller;

import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionResponse;
import com.dacia1704.truyenonline.module.payment.service.SubscriptionService;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionController {
    SubscriptionService subscriptionService;

    @GetMapping("/me")
    public ApiResponse<SubscriptionResponse> getSubscriptionPlans() {
        SubscriptionResponse result = subscriptionService.getMySubscription();
        return ApiResponse.success(result);
    }
}
