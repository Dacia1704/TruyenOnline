package com.dacia1704.truyenonline.module.payment.dto.response;

import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentResponse {
    String txnRef;
    String paymentUrl; // URL redirect sang VNPay
    Long amount; // Số tiền VND (chưa × 100)
    SubscriptionPlan plan;
}
