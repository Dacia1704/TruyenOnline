package com.dacia1704.truyenonline.module.interaction.service;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryFilter;
import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.entity.HistoryType;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import com.dacia1704.truyenonline.module.interaction.mapper.ReadingHistoryMapper;
import com.dacia1704.truyenonline.module.interaction.repository.ReadingHistoryRepository;
import com.dacia1704.truyenonline.module.interaction.repository.specification.ReadingHistorySpecification;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ReadingHistoryService {
    ChapterRepository chapterRepository;
    UserRepository userRepository;
    ReadingHistoryRepository readingHistoryRepository;
    ReadingHistoryMapper readingHistoryMapper;
    StoryRepository storyRepository;

    public PageResponse<ReadingHistoryResponse> getMyReadingHistory(
            int page, int size, ReadingHistoryFilter filter) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        int pageNo = Math.max(page - 1, 0);
        Pageable pageable =
                PageRequest.of(pageNo, size, Sort.by(Sort.Direction.DESC, "lastReadAt"));

        String userId = null;

        if (isAuthenticated) {
            userId = authentication.getName();
        } else {
            if (filter.getSessionId() == null || filter.getSessionId().isBlank()) {
                throw new AppException(ErrorCode.SESSION_INVALID);
            }
        }

        Specification<ReadingHistory> specification =
                ReadingHistorySpecification.filter(userId, filter.getSessionId(), filter);

        Page<ReadingHistory> readingHistories =
                readingHistoryRepository.findAll(specification, pageable);

        List<ReadingHistoryResponse> responses =
                readingHistories.getContent().stream()
                        .map(readingHistoryMapper::toReadingHistoryResponse)
                        .toList();

        return PageResponse.<ReadingHistoryResponse>builder()
                .currentPage(page)
                .pageSize(readingHistories.getSize())
                .totalPages(readingHistories.getTotalPages())
                .totalElements(readingHistories.getTotalElements())
                .data(responses)
                .build();
    }

    public ReadingHistoryResponse getLastReadingChapterInStory(String sessionId, String storyId) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());
        Optional<ReadingHistory> optional;
        if (isAuthenticated) {
            String userId = authentication.getName();
            optional =
                    readingHistoryRepository.findByStoryIdAndUserIdAndType(
                            storyId, userId, HistoryType.CHAPTER);
        } else {
            if (sessionId == null || sessionId.isBlank()) return null;
            optional =
                    readingHistoryRepository.findByStoryIdAndSessionIdAndType(
                            storyId, sessionId, HistoryType.CHAPTER);
        }
        return optional.map(readingHistoryMapper::toReadingHistoryResponse).orElse(null);
    }

    @Transactional
    public ReadingHistoryResponse updateMyReadingHistory(
            String sessionId, ReadingHistoryRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        Story story =
                storyRepository
                        .findById(request.getStoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        String chapterIdToSet = null;

        if (request.getType() == HistoryType.CHAPTER) {
            if (request.getChapterId() == null || request.getChapterId().isBlank()) {
                throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
            }
            Chapter chapter =
                    chapterRepository
                            .findById(request.getChapterId())
                            .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

            if (!chapter.getStory().getId().equals(story.getId())) {
                throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
            }
            chapterIdToSet = chapter.getId();
        } else if (request.getType() != HistoryType.STORY) {
            throw new IllegalArgumentException("Unsupported history type: " + request.getType());
        }

        ReadingHistory readingHistory;

        if (isAuthenticated) {
            String userId = authentication.getName();
            userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            readingHistory = readingHistoryRepository
                    .findByStoryIdAndUserIdAndType(story.getId(), userId, request.getType())
                    .orElse(null);

            String id = readingHistory != null ? readingHistory.getId() : java.util.UUID.randomUUID().toString();

            readingHistoryRepository.upsertByUser(
                    id, userId, story.getId(), chapterIdToSet, request.getType().name());

            readingHistory = readingHistoryRepository
                    .findByStoryIdAndUserIdAndType(story.getId(), userId, request.getType())
                    .orElse(null);

        } else {
            if (sessionId == null || sessionId.isBlank()) {
                throw new AppException(ErrorCode.SESSION_INVALID);
            }

            readingHistory = readingHistoryRepository
                    .findByStoryIdAndSessionIdAndType(story.getId(), sessionId, request.getType())
                    .orElse(null);

            String id = readingHistory != null ? readingHistory.getId() : java.util.UUID.randomUUID().toString();

            readingHistoryRepository.upsertByGuest(
                    id, sessionId, story.getId(), chapterIdToSet, request.getType().name());

            readingHistory = readingHistoryRepository
                    .findByStoryIdAndSessionIdAndType(story.getId(), sessionId, request.getType())
                    .orElse(null);
        }

        // Tăng view count sau khi upsert thành công
        if (request.getType() == HistoryType.STORY) {
            storyRepository.incrementViewCount(story.getId());
        } else {
            chapterRepository.incrementViewCount(chapterIdToSet);
        }

        return readingHistoryMapper.toReadingHistoryResponse(readingHistory);
    }

    public void deleteMyReadingHistory(String sessionId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        if (isAuthenticated) {
            String userId = authentication.getName();
            readingHistoryRepository.deleteByUserId(userId);
        } else {
            readingHistoryRepository.deleteBySessionId(sessionId);
        }
    }

    public void deleteReadingHistoryByChapter(String chapterId) {
        readingHistoryRepository.deleteByChapterId(chapterId);
    }

    public void deleteReadingHistoryByStory(String storyId) {
        readingHistoryRepository.deleteByStoryId(storyId);
    }

    public void mergeSessionHistory(String userId, String sessionId) {
        if (userId == null) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }
        ReadingHistoryFilter filter = ReadingHistoryFilter.builder().sessionId(sessionId).build();
        Specification<ReadingHistory> specification =
                ReadingHistorySpecification.filter(null, filter.getSessionId(), filter);

        List<ReadingHistory> readingHistories = readingHistoryRepository.findAll(specification);

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        readingHistories.forEach(readingHistory -> readingHistory.setUser(user));
        readingHistoryRepository.saveAll(readingHistories);
    }
}
