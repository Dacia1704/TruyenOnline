package com.dacia1704.truyenonline.module.payment.repository;

import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String> {
    List<SubscriptionPlan> findAllByIsActive(Boolean isActive);
}
