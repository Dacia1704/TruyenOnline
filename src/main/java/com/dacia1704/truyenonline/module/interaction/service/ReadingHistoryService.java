package com.dacia1704.truyenonline.module.interaction.service;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.interaction.dto.request.ReadingHistoryRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import com.dacia1704.truyenonline.module.interaction.mapper.ReadingHistoryMapper;
import com.dacia1704.truyenonline.module.interaction.repository.ReadingHistoryRepository;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    public PageResponse<ReadingHistoryResponse> getMyReadingHistory(
            int page, int size, String sessionId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("lastReadAt").descending());

        Page<ReadingHistory> readingHistories;

        if (isAuthenticated) {
            String userId = authentication.getName();
            readingHistories = readingHistoryRepository.findByUserId(userId, pageable);
        } else {
            if (sessionId == null || sessionId.isBlank()) {
                throw new AppException(ErrorCode.SESSION_INVALID);
            }
            readingHistories = readingHistoryRepository.findBySessionId(sessionId, pageable);
        }

        List<ReadingHistoryResponse> readingHistoryResponses =
                readingHistories.getContent().stream()
                        .map(readingHistoryMapper::toReadingHistoryResponse)
                        .toList();

        return PageResponse.<ReadingHistoryResponse>builder()
                .currentPage(page)
                .pageSize(readingHistories.getSize())
                .totalPages(readingHistories.getTotalPages())
                .totalElements(readingHistories.getTotalElements())
                .data(readingHistoryResponses)
                .build();
    }

    public ReadingHistoryResponse createMyReadingHistory(
            String sessionId, ReadingHistoryRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(request.getChapterId())
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Story story = chapter.getStory();

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        ReadingHistory readingHistory;

        if (isAuthenticated) {
            String userId = authentication.getName();
            User user =
                    userRepository
                            .findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            readingHistory =
                    readingHistoryRepository
                            .findByStoryIdAndUserId(story.getId(), userId)
                            .orElseGet(
                                    () -> ReadingHistory.builder().story(story).user(user).build());
        } else {
            if (sessionId == null || sessionId.isBlank()) {
                throw new AppException(ErrorCode.SESSION_INVALID);
            }

            readingHistory =
                    readingHistoryRepository
                            .findByStoryIdAndSessionId(story.getId(), sessionId)
                            .orElseGet(
                                    () ->
                                            ReadingHistory.builder()
                                                    .story(story)
                                                    .sessionId(sessionId)
                                                    .build());
        }

        readingHistory.setChapter(chapter);
        readingHistory = readingHistoryRepository.save(readingHistory);
        return readingHistoryMapper.toReadingHistoryResponse(readingHistory);
    }

    public ReadingHistoryResponse updateMyReadingHistory(
            String sessionId, ReadingHistoryRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(request.getChapterId())
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Story story = chapter.getStory();

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated =
                authentication != null
                        && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal());

        ReadingHistory readingHistory;

        if (isAuthenticated) {
            String userId = authentication.getName();
            User user =
                    userRepository
                            .findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            readingHistory =
                    readingHistoryRepository
                            .findByStoryIdAndUserId(story.getId(), userId)
                            .orElseGet(
                                    () -> ReadingHistory.builder().story(story).user(user).build());
        } else {
            if (sessionId == null || sessionId.isBlank()) {
                throw new AppException(ErrorCode.SESSION_INVALID);
            }

            readingHistory =
                    readingHistoryRepository
                            .findByStoryIdAndSessionId(story.getId(), sessionId)
                            .orElseGet(
                                    () ->
                                            ReadingHistory.builder()
                                                    .story(story)
                                                    .sessionId(sessionId)
                                                    .build());
        }

        readingHistory.setChapter(chapter);
        readingHistory = readingHistoryRepository.save(readingHistory);
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
}
