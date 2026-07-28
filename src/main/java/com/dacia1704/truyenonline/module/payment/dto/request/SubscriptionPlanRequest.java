package com.dacia1704.truyenonline.module.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanRequest {
    String code;
    String name;
    String description;
    Long price;
    Integer durationDays;
    Boolean isActive;
    Byte sortOrder;
}