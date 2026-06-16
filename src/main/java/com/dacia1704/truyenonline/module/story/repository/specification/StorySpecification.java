package com.dacia1704.truyenonline.module.story.repository.specification;

import com.dacia1704.truyenonline.module.story.dto.request.StoryFilter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StorySpecification {
    public static Specification<Story> filterStories(String search, StoryFilter filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm (Search) theo title hoặc title_no_accent
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate titleNoAccentPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("titleNoAccent")), searchPattern);

                // Điều kiện: title LIKE %search% OR titleNoAccent LIKE %search%
                predicates.add(criteriaBuilder.or(titlePredicate, titleNoAccentPredicate));
            }

            // 2. Lọc (Filter) theo thể loại
            if (filters.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("storyType"), filters.getType()));
            }

            // 3. Lọc (Filter) theo trạng thái
            if (filters.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filters.getStatus()));
            }

            // 4. Lọc (Filter) theo trạng thái xuất bản
            if (filters.getIsPublished() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPublished"), filters.getIsPublished()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
