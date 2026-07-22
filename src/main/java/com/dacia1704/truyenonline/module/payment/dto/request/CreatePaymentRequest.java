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

    @NotNull(message = "Vui lòng chọn gói")
    private SubscriptionPlan plan;

    private String orderInfo;
}