package com.dacia1704.truyenonline.module.administration.repository;

import com.dacia1704.truyenonline.module.administration.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, String>, JpaSpecificationExecutor<AuditLog> {}
