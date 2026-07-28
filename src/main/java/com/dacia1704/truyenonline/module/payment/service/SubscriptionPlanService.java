package com.dacia1704.truyenonline.module.payment.service;

import com.dacia1704.truyenonline.module.payment.dto.request.SubscriptionPlanRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionPlanResponse;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.mapper.SubscriptionPlanMapper;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionPlanRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SubscriptionPlanService {
    SubscriptionPlanRepository subscriptionPlanRepository;
    SubscriptionPlanMapper subscriptionPlanMapper;

    public List<SubscriptionPlanResponse> getPlans(Boolean isActive) {
        List<SubscriptionPlan> plans =subscriptionPlanRepository.findAllByIsActive(isActive);
        return plans.stream().map(subscriptionPlanMapper::toSubscriptionPlanResponse).toList();
    }

    public SubscriptionPlanResponse getPlan(String code) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(code).orElseThrow(()-> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanMapper.toSubscriptionPlan(request);
        plan = subscriptionPlanRepository.save(plan);
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public SubscriptionPlanResponse updatePlan(String code, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(code).orElseThrow(()-> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        subscriptionPlanMapper.updateSubscriptionPlan(plan, request);
        plan = subscriptionPlanRepository.save(plan);
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public void deletePlan(String code) {
        subscriptionPlanRepository.deleteById(code);
    }
}
