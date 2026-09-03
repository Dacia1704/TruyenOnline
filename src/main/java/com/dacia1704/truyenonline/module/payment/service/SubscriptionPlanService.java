package com.dacia1704.truyenonline.module.payment.service;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.payment.dto.request.SubscriptionPlanRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionPlanResponse;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.mapper.SubscriptionPlanMapper;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionPlanRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SubscriptionPlanService {
    SubscriptionPlanRepository subscriptionPlanRepository;
    SubscriptionPlanMapper subscriptionPlanMapper;
    AuditLogService auditLogService;

    public List<SubscriptionPlanResponse> getPlans(Boolean isActive) {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAllByIsActive(isActive);
        return plans.stream().map(subscriptionPlanMapper::toSubscriptionPlanResponse).toList();
    }

    public SubscriptionPlanResponse getPlan(String code) {
        SubscriptionPlan plan =
                subscriptionPlanRepository
                        .findById(code)
                        .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanMapper.toSubscriptionPlan(request);
        plan = subscriptionPlanRepository.save(plan);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.SUBSCRIPTION_PLAN,
                plan.getCode(),
                null,
                buildAuditLogSubscriptionPlan(plan),
                null);
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public SubscriptionPlanResponse updatePlan(String code, SubscriptionPlanRequest request) {
        SubscriptionPlan plan =
                subscriptionPlanRepository
                        .findById(code)
                        .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        var oldValue = buildAuditLogSubscriptionPlan(plan);
        subscriptionPlanMapper.updateSubscriptionPlan(plan, request);
        plan = subscriptionPlanRepository.save(plan);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.SUBSCRIPTION_PLAN,
                plan.getCode(),
                oldValue,
                buildAuditLogSubscriptionPlan(plan),
                null);

        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    public void deletePlan(String code) {
        SubscriptionPlan plan =
                subscriptionPlanRepository
                        .findById(code)
                        .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        subscriptionPlanRepository.deleteById(code);
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.SUBSCRIPTION_PLAN,
                plan.getCode(),
                buildAuditLogSubscriptionPlan(plan),
                null,
                null);
    }

    public Map<String, Object> buildAuditLogSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        if (subscriptionPlan == null) {
            return Map.of();
        }

        return AuditMapper.of(subscriptionPlan)
                .add("code", SubscriptionPlan::getCode)
                .add("name", SubscriptionPlan::getName)
                .add("description", SubscriptionPlan::getDescription)
                .add("price", SubscriptionPlan::getPrice)
                .add("durationDays", SubscriptionPlan::getDurationDays)
                .add("isActive", SubscriptionPlan::getIsActive)
                .build();
    }
}
