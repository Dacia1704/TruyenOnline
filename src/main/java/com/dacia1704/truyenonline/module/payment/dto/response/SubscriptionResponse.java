package com.dacia1704.truyenonline.module.payment.dto.response;

import com.dacia1704.truyenonline.module.payment.entity.SubscriptionStatus;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionResponse {
    String id;
    UserResponse user;
    SubscriptionStatus status;
    LocalDateTime startedAt;
    LocalDateTime expiresAt;
    SubscriptionPlanResponse plan;
}
