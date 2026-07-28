package com.dacia1704.truyenonline.module.payment.repository;

import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String> {
    List<SubscriptionPlan> findAllByIsActive(Boolean isActive);
}