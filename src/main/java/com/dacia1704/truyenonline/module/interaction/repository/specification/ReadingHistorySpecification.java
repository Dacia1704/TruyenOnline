package com.dacia1704.truyenonline.module.interaction.repository.specification;

import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryFilter;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ReadingHistorySpecification {
    public static Specification<ReadingHistory> filter(
            String userId, String sessionId, ReadingHistoryFilter filter) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (userId == null && sessionId != null) {
                predicates.add(cb.equal(root.get("sessionId"), sessionId));
            }

            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            if (filter.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("lastReadAt"), filter.getFromDate().atStartOfDay()));
            }

            if (filter.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("lastReadAt"), filter.getToDate().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
