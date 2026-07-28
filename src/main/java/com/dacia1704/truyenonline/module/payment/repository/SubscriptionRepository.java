package com.dacia1704.truyenonline.module.payment.repository;

import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    @Query("""
        SELECT s FROM Subscription s
        WHERE s.user.id = :userId
          AND s.status = 'ACTIVE'
          AND s.expiresAt > :now
        ORDER BY s.expiresAt DESC
        LIMIT 1
    """)
    Optional<Subscription> findActiveByUser(String userId, LocalDateTime now);

    Optional<Subscription> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}