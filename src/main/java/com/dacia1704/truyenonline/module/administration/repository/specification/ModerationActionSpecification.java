package com.dacia1704.truyenonline.module.administration.repository.specification;

import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ModerationActionSpecification {

    public static Specification<ModerationAction> filterModerationActions(
            String objectId,
            ModerationObjectType objectType,
            ModerationActionType actionType,
            ViolationType violationType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(objectId)) {
                predicates.add(criteriaBuilder.equal(root.get("objectId"), objectId));
            }

            if (objectType != null) {
                predicates.add(criteriaBuilder.equal(root.get("objectType"), objectType));
            }

            if (actionType != null) {
                predicates.add(criteriaBuilder.equal(root.get("actionType"), actionType));
            }

            if (violationType != null) {
                predicates.add(criteriaBuilder.equal(root.get("violationType"), violationType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
