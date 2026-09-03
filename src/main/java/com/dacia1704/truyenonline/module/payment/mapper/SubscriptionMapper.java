package com.dacia1704.truyenonline.module.payment.mapper;

import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionResponse;
import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);
}
