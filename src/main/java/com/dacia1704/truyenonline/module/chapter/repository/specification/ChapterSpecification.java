package com.dacia1704.truyenonline.module.chapter.repository.specification;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ChapterSpecification {
    public static Specification<Chapter> filterChapters(String search, Story story) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm (Search) theo title hoặc title_no_accent
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate titleNoAccentPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("titleNoAccent")), searchPattern);

                // Điều kiện: title LIKE %search% OR titleNoAccent LIKE %search%
                predicates.add(criteriaBuilder.or(titlePredicate, titleNoAccentPredicate));
            }
            if (story != null) {
                predicates.add(criteriaBuilder.equal(root.get("story").get("id"), story.getId()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
