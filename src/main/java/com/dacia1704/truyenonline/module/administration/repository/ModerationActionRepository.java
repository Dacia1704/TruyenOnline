package com.dacia1704.truyenonline.module.administration.repository;

import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ModerationActionRepository extends JpaRepository<ModerationAction, String>, JpaSpecificationExecutor<ModerationAction> {
}
