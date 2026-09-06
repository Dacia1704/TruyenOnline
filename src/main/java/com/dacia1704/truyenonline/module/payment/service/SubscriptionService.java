package com.dacia1704.truyenonline.module.payment.service;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.payment.dto.response.SubscriptionResponse;
import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionStatus;
import com.dacia1704.truyenonline.module.payment.mapper.SubscriptionMapper;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final AuditLogService auditLogService;

    // ----------------------------------------------------------------
    // Kích hoạt subscription sau khi thanh toán thành công
    // Nếu user đang có gói chưa hết hạn → gia hạn thêm
    // ----------------------------------------------------------------
    @Transactional
    public Subscription activateSubscription(User user, SubscriptionPlan plan) {
        Integer days = plan.getDurationDays();
        if (days == null) throw new AppException(ErrorCode.INVALID_PLAN);

        LocalDateTime now = LocalDateTime.now();
        // Kiểm tra gói hiện tại còn hạn không
        Optional<Subscription> existingOpt =
                subscriptionRepository.findActiveByUser(user.getId(), now);

        LocalDateTime startedAt;
        LocalDateTime expiresAt;

        if (existingOpt.isPresent()) {
            // Gia hạn từ ngày hết hạn hiện tại
            Subscription existing = existingOpt.get();
            startedAt = now;
            expiresAt = existing.getExpiresAt().plusDays(days);

            // Hủy gói cũ
            existing.setStatus(SubscriptionStatus.CANCELLED);
            subscriptionRepository.save(existing);

            log.info(
                    "Gia hạn subscription user={}, từ {} thêm {} ngày",
                    user.getId(),
                    existing.getExpiresAt(),
                    days);
        } else {
            // Tạo gói mới
            startedAt = now;
            expiresAt = now.plusDays(days);

            log.info(
                    "Tạo mới subscription user={}, plan={}, hết hạn={}",
                    user.getId(),
                    plan,
                    expiresAt);
        }

        Subscription subscription =
                Subscription.builder()
                        .user(user)
                        .plan(plan)
                        .status(SubscriptionStatus.ACTIVE)
                        .startedAt(startedAt)
                        .expiresAt(expiresAt)
                        .build();

        subscription = subscriptionRepository.save(subscription);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.SUBSCRIPTION,
                subscription.getId(),
                null,
                buildAuditLogSubscription(subscription),
                null,
                user.getId());
        return subscription;
    }

    // ----------------------------------------------------------------
    // Kiểm tra user có Premium đang còn hạn không
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public boolean isPremium(String userId) {
        return subscriptionRepository.findActiveByUser(userId, LocalDateTime.now()).isPresent();
    }

    public SubscriptionResponse getMySubscription() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return subscriptionRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(subscriptionMapper::toSubscriptionResponse)
                .orElse(null);
    }

    public Map<String, Object> buildAuditLogSubscription(Subscription subscription) {
        if (subscription == null) {
            return Map.of();
        }

        return AuditMapper.of(subscription)
                .add("id", Subscription::getId)
                .add("user", s -> s.getUser().getId())
                .add("status", Subscription::getStatus)
                .add("startedAt", Subscription::getStartedAt)
                .add("expiresAt", Subscription::getExpiresAt)
                .add("plan", s -> s.getPlan().getCode())
                .build();
    }
}
