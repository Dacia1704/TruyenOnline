package com.dacia1704.truyenonline.module.payment.dto.response;

import com.dacia1704.truyenonline.module.payment.entity.TransactionStatus;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {
    String id;
    String vnpTxnRef;
    SubscriptionPlanResponse subscriptionPlan;
    UserResponse user;
    Long amountVnd;        // Đã chia 100, hiển thị được luôn
    TransactionStatus status;
    String vnpBankCode;
    String vnpTransactionNo;
    LocalDateTime createdAt;
    LocalDateTime completedAt;
}