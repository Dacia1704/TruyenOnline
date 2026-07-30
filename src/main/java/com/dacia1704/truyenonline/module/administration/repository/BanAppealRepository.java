package com.dacia1704.truyenonline.module.administration.repository;

import com.dacia1704.truyenonline.module.administration.entity.BanAppeal;
import com.dacia1704.truyenonline.module.administration.entity.BanAppealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanAppealRepository extends JpaRepository<BanAppeal, String> {

    Page<BanAppeal> findAllByUser_IdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    Page<BanAppeal> findAllByStatusOrderByCreatedAtDesc(
            BanAppealStatus status,
            Pageable pageable
    );

    Page<BanAppeal> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    boolean existsByModerationAction_Id(String moderationActionId);
}