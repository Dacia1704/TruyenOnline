package com.dacia1704.truyenonline.module.story.repository.specification;

import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import com.dacia1704.truyenonline.module.story.dto.request.StoryFilter;
import com.dacia1704.truyenonline.module.story.entity.Genre;
import com.dacia1704.truyenonline.module.story.entity.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class StorySpecification {
    public static Specification<Story> filterStories(StoryFilter filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm (Search) theo title hoặc title_no_accent
            if (StringUtils.hasText(filters.getSearch())) {
                String searchPattern = "%" + filters.getSearch().toLowerCase() + "%";
                Predicate titlePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate titleNoAccentPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("titleNoAccent")), searchPattern);

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
                predicates.add(
                        criteriaBuilder.equal(root.get("isPublished"), filters.getIsPublished()));
            }


            // 5. Lọc (Filter) theo Thể loại (Genre) - Xử lý Many-To-Many
            if (filters.getGenres() != null && !filters.getGenres().isEmpty()) {
                Join<Story, Genre> genreJoin = root.join("genres");

                predicates.add(genreJoin.get("slug").in(filters.getGenres()));

                query.distinct(true);
            }

            // 6. Uploader
            if (filters.getUploaderId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("uploader").get("id"), filters.getUploaderId())
                );
            }

            // 7. Sort
            if (filters.getSortType() != null && query != null) {
                switch (filters.getSortType()) {
                    case NEWEST -> query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                    case OLDEST -> query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
                    case UPDATED -> query.orderBy(criteriaBuilder.desc(root.get("updatedAt")));
                    case VIEW -> query.orderBy(criteriaBuilder.desc(root.get("viewCount")));
                    case ALPHABET_ASC -> query.orderBy(criteriaBuilder.asc(root.get("title")));
                    case ALPHABET_DESC -> query.orderBy(criteriaBuilder.desc(root.get("title")));
                    case FOLLOW -> {
                        Join<Story, Bookmark> bookmarkJoin = root.join("bookmarks", JoinType.LEFT);

                        query.groupBy(root.get("id"));

                        query.orderBy(
                                criteriaBuilder.desc(criteriaBuilder.count(bookmarkJoin))
                        );
                    }
                }
            } else if (query != null) {
                query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
