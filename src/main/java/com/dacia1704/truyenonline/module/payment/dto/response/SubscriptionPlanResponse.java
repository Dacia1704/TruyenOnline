package com.dacia1704.truyenonline.module.payment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanResponse {
    String code;
    String name;
    String description;
    Long price;
    Integer durationDays;
    Boolean isActive;
    Byte sortOrder;
}