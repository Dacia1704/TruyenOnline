package com.dacia1704.truyenonline.module.administration.repository.specification;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditLog;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AuditLogSpecification {
    public static Specification<AuditLog> filterAuditLogs(
            String actorId,
            AuditAction action,
            AuditObjectType objectType,
            String objectId,
            LocalDateTime fromDate,
    LocalDateTime toDate
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(actorId)) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("actor").get("id"),
                                actorId
                        )
                );
            }

            if (action != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("action"),
                                action
                        )
                );
            }

            if (objectType != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("objectType"),
                                objectType
                        )
                );
            }

            if (StringUtils.hasText(objectId)) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("objectId"),
                                objectId
                        )
                );
            }

            if (fromDate != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                fromDate
                        )
                );
            }

            if (toDate != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                toDate
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private AuditLogSpecification() {
    }
}
