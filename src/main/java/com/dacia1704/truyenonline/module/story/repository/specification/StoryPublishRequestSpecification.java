package com.dacia1704.truyenonline.module.story.repository.specification;

import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequest;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class StoryPublishRequestSpecification {
    public static Specification<StoryPublishRequest> filterRequests(
            String storyId, StoryPublishRequestStatus status, String uploaderId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc (Filter) theo storyId
            if (StringUtils.hasText(storyId)) {
                predicates.add(criteriaBuilder.equal(root.get("story").get("id"), storyId));
            }

            // 2. Lọc (Filter) theo trạng thái duyệt
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // 3. Lọc (Filter) theo uploaderId (Chủ sở hữu của truyện)
            if (StringUtils.hasText(uploaderId)) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("story").get("uploader").get("id"), uploaderId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
