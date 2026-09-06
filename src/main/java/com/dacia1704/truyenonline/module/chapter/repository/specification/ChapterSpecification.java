package com.dacia1704.truyenonline.module.chapter.repository.specification;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ChapterSpecification {
    private ChapterSpecification() {
        /* This utility class should not be instantiated */
    }

    public static Specification<Chapter> filterChapters(
            String search,
            Story story,
            Integer from,
            BigDecimal freeChapterLimit,
            Boolean isBanned,
            Boolean isPublished) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")), searchPattern);

                Predicate titleNoAccentPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("titleNoAccent")), searchPattern);

                predicates.add(criteriaBuilder.or(titlePredicate, titleNoAccentPredicate));
            }

            if (story != null)
                predicates.add(criteriaBuilder.equal(root.get("story").get("id"), story.getId()));

            if (isBanned != null)
                predicates.add(criteriaBuilder.equal(root.get("isBanned"), isBanned));

            if (isPublished != null)
                predicates.add(criteriaBuilder.equal(root.get("isPublished"), isPublished));

            if (from != null && from > 0)
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("chapterNumber"), BigDecimal.valueOf(from)));

            if (freeChapterLimit != null)
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("chapterNumber"), freeChapterLimit));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
