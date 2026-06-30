package com.dacia1704.truyenonline.module.billing.repository;

import com.dacia1704.truyenonline.module.billing.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {}
