package com.dacia1704.truyenonline.module.payment.dto.request;

import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {

    private String planId;

    private String orderInfo;
}