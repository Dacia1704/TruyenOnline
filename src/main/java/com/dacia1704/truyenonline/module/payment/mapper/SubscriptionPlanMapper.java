package com.dacia1704.truyenonline.module.payment.mapper;

import com.dacia1704.truyenonline.module.payment.dto.request.SubscriptionPlanRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionPlanResponse;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.entity.Genre;
import com.dacia1704.truyenonline.module.user.dto.request.UserUpdateRequest;
import com.dacia1704.truyenonline.module.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {
    SubscriptionPlan toSubscriptionPlan(SubscriptionPlanRequest request);

    SubscriptionPlanResponse toSubscriptionPlanResponse(SubscriptionPlan plan);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "code", ignore = true)
    void updateSubscriptionPlan(@MappingTarget SubscriptionPlan plan, SubscriptionPlanRequest request);
}
