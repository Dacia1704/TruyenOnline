package com.dacia1704.truyenonline.module.story.repository.specification;

import com.dacia1704.truyenonline.module.story.entity.Genre;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class GenreSpecification {
    public static Specification<Genre> filterGenres(String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm (Search) theo name hoặc name_no_accent
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate nameNoAccentPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("nameNoAccent")), searchPattern);

                // Điều kiện: title LIKE %search% OR titleNoAccent LIKE %search%
                predicates.add(criteriaBuilder.or(namePredicate, nameNoAccentPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
