package com.dacia1704.truyenonline.module.payment.service;

import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionStatus;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    // Số ngày tương ứng với từng gói
    private static final Map<SubscriptionPlan, Integer> PLAN_DAYS = Map.of(
            SubscriptionPlan.PREMIUM_1M,  30,
            SubscriptionPlan.PREMIUM_3M,  90,
            SubscriptionPlan.PREMIUM_1Y, 365
    );

    // ----------------------------------------------------------------
    // Kích hoạt subscription sau khi thanh toán thành công
    // Nếu user đang có gói chưa hết hạn → gia hạn thêm
    // ----------------------------------------------------------------
    @Transactional
    public Subscription activateSubscription(User user, SubscriptionPlan plan) {
        Integer days = PLAN_DAYS.get(plan);
        if (days == null) {
            throw new IllegalArgumentException("Gói không hợp lệ: " + plan);
        }

        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra gói hiện tại còn hạn không
        Optional<Subscription> existingOpt = subscriptionRepository
                .findActiveByUser(user.getId(), now);

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

            log.info("Gia hạn subscription user={}, từ {} thêm {} ngày",
                    user.getId(), existing.getExpiresAt(), days);
        } else {
            // Tạo gói mới
            startedAt = now;
            expiresAt = now.plusDays(days);

            log.info("Tạo mới subscription user={}, plan={}, hết hạn={}",
                    user.getId(), plan, expiresAt);
        }

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(startedAt)
                .expiresAt(expiresAt)
                .build();

        return subscriptionRepository.save(subscription);
    }

    // ----------------------------------------------------------------
    // Kiểm tra user có Premium đang còn hạn không
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public boolean isPremium(String userId) {
        return subscriptionRepository
                .findActiveByUser(userId, LocalDateTime.now())
                .isPresent();
    }
}