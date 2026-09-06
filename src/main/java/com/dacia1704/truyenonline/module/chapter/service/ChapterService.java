package com.dacia1704.truyenonline.module.chapter.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.dto.request.*;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterPageResponse;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.mapper.ChapterMapper;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.chapter.repository.specification.ChapterSpecification;
import com.dacia1704.truyenonline.module.interaction.service.CommentService;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.module.payment.entity.Subscription;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionRepository;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ChapterService {
    ChapterRepository chapterRepository;
    ChapterMapper chapterMapper;
    StoryRepository storyRepository;
    ChapterPageService chapterPageService;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;
    UserRepository userRepository;
    SubscriptionRepository subscriptionRepository;
    CommentService commentService;
    ReadingHistoryService readingHistoryService;

    public PageResponse<ChapterResponse> getChaptersBySlugStory(
            int page, int size, Integer from, String slug, String search) {
        Story story =
                storyRepository
                        .findBySlug(slug)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("chapterNumber").ascending());

        Specification<Chapter> spec =
                ChapterSpecification.filterChapters(search, story, from, null, false, true);

        Page<Chapter> chapterPage = chapterRepository.findAll(spec, pageable);
        List<ChapterResponse> chapterResponses =
                chapterPage.getContent().stream().map(chapterMapper::toChapterResponse).toList();
        return PageResponse.<ChapterResponse>builder()
                .currentPage(page)
                .pageSize(chapterPage.getSize())
                .totalPages(chapterPage.getTotalPages())
                .totalElements(chapterPage.getTotalElements())
                .data(chapterResponses)
                .build();
    }

    public PageResponse<ChapterResponse> getChaptersByStoryId(
            int page, int size, Integer from, String id, String search) {
        Story story =
                storyRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isUploaderOrAdmin =
                storyRepository.isStoryUploader(story.getId(),userId)
                        || user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (Boolean.TRUE.equals(story.getIsBanned()) && !isUploaderOrAdmin) {
            throw new AppException(ErrorCode.STORY_ALREADY_BANNED);
        }
        if (Boolean.FALSE.equals(story.getIsPublished()) && !isUploaderOrAdmin) {
            throw new AppException(ErrorCode.STORY_NOT_PUBLISHED);
        }

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("chapterNumber").ascending());

        Specification<Chapter> spec =
                ChapterSpecification.filterChapters(search, story, from, null, null, null);

        Page<Chapter> chapterPage = chapterRepository.findAll(spec, pageable);
        List<ChapterResponse> chapterResponses =
                chapterPage.getContent().stream().map(chapterMapper::toChapterResponse).toList();
        return PageResponse.<ChapterResponse>builder()
                .currentPage(page)
                .pageSize(chapterPage.getSize())
                .totalPages(chapterPage.getTotalPages())
                .totalElements(chapterPage.getTotalElements())
                .data(chapterResponses)
                .build();
    }

    public ChapterResponse getChapterById(String chapterId) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        if (chapter.getStory().getFreeChapterLimit() != null
                && BigDecimal.valueOf(chapter.getStory().getFreeChapterLimit())
                                .compareTo(chapter.getChapterNumber())
                        < 0) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean authenticated =
                    authentication != null
                            && authentication.isAuthenticated()
                            && !(authentication instanceof AnonymousAuthenticationToken);
            if (authenticated) {
                String userId = authentication.getName();
                User user =
                        userRepository
                                .findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Subscription subscription =
                        subscriptionRepository
                                .findActiveByUser(authentication.getName(), LocalDateTime.now())
                                .orElse(null);

                boolean isUploader = chapterRepository.isChapterUploader(chapterId, userId);
                boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
                if (subscription == null && !(isUploader || isAdmin)) {
                    throw new AppException(ErrorCode.PREMIUM_REQUIRED);
                }
            } else {
                throw new AppException(ErrorCode.PREMIUM_REQUIRED);
            }
        }
        List<ChapterPageResponse> pages = chapterPageService.getChapterPagesByChapterId(chapterId);
        ChapterResponse res = chapterMapper.toChapterResponse(chapter);
        res.setContent(chapter.getContent());
        res.setPages(pages);
        return res;
    }

    public ChapterResponse createChapter(String storyId, ChapterCreateRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setTitleNoAccent(StringUtils.removeAccent(chapter.getTitle()));
        chapter.setStory(story);

        BigDecimal maxChapter = chapterRepository.findMaxChapterNumberByStoryId(storyId);
        if (maxChapter == null) {
            maxChapter = BigDecimal.ZERO;
        }
        BigDecimal nextChapter =
                maxChapter
                        .setScale(0, RoundingMode.FLOOR)
                        .add(BigDecimal.ONE)
                        .setScale(1, RoundingMode.UNNECESSARY);

        chapter.setChapterNumber(nextChapter);
        chapter = chapterRepository.save(chapter);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                null,
                buildAuditLogChapter(chapter),
                null);
        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse updateChapter(String chapterId, ChapterUpdateRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        var oldValue = buildAuditLogChapter(chapter);

        chapterMapper.updateChapter(chapter, request);
        chapter = chapterRepository.save(chapter);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                oldValue,
                buildAuditLogChapter(chapter),
                null);

        return chapterMapper.toChapterResponse(chapter);
    }

    public void updatePublishStatusChapters(ChapterUpdatePublishStatusRequest request) {
        List<Chapter> chapters = chapterRepository.findAllById(request.getChapterIdList());
        var context = SecurityContextHolder.getContext();
        String uploaderId = context.getAuthentication().getName();

        // check chapter có cùng story ko
        if (chapters.isEmpty()) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        String storyId = chapters.getFirst().getStory().getId();
        boolean sameStory =
                chapters.stream().allMatch(chapter -> chapter.getStory().getId().equals(storyId));
        if (!sameStory) {
            throw new AppException(ErrorCode.CHAPTER_NOT_SAME_STORY);
        }

        // check story có phải của uploader này ko
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (!uploaderId.equals(story.getUploader().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        chapters.forEach(
                chapter -> {
                    var oldValue = buildAuditLogChapter(chapter);
                    chapter.setIsPublished(request.getPublishStatus());
                    auditLogService.log(
                            AuditAction.PUBLISH,
                            AuditObjectType.CHAPTER,
                            chapter.getId(),
                            oldValue,
                            buildAuditLogChapter(chapter),
                            null);
                });

        chapterRepository.saveAll(chapters);
    }

    public ChapterResponse updateChapterContent(
            String chapterId, ChapterContentUpdateRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        var oldValue = buildAuditLogChapter(chapter);
        chapter.setContent(request.getContent());
        chapter = chapterRepository.save(chapter);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                oldValue,
                buildAuditLogChapter(chapter),
                null);

        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse banChapter(String chapterId, ChapterBanRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        if (Boolean.TRUE.equals(chapter.getIsBanned()))
            throw new AppException(ErrorCode.CHAPTER_ALREADY_BANNED);
        var oldValue = buildAuditLogChapter(chapter);
        chapter.setIsBanned(true);
        chapter = chapterRepository.save(chapter);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(chapterId)
                        .objectType(ModerationObjectType.CHAPTER)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.BAN,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                oldValue,
                buildAuditLogChapter(chapter),
                request.getReason());
        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse unbanChapter(String chapterId, ChapterUnbanRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        if (Boolean.FALSE.equals(chapter.getIsBanned()))
            throw new AppException(ErrorCode.CHAPTER_NOT_GET_BANNED);
        var oldValue = buildAuditLogChapter(chapter);
        chapter.setIsBanned(false);
        chapter = chapterRepository.save(chapter);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(chapterId)
                        .objectType(ModerationObjectType.CHAPTER)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.UNBAN,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                oldValue,
                buildAuditLogChapter(chapter),
                request.getReason());
        return chapterMapper.toChapterResponse(chapter);
    }

    public void deleteChapter(String chapterId) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        var context = SecurityContextHolder.getContext();
        String uploaderId = context.getAuthentication().getName();
        if (!chapterRepository.isChapterUploader(chapter.getId(), uploaderId))
            throw new AppException(ErrorCode.NO_PERMISSION);

        chapterPageService.deleteChapterPageByChapter(chapterId);

        commentService.deleteCommentByChapter(chapterId);

        readingHistoryService.deleteReadingHistoryByChapter(chapterId);

        chapterRepository.deleteById(chapterId);
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.CHAPTER,
                chapter.getId(),
                buildAuditLogChapter(chapter),
                null,
                null);
    }

    private Map<String, Object> buildAuditLogChapter(Chapter chapter) {
        if (chapter == null) {
            return Map.of();
        }

        return AuditMapper.of(chapter)
                .add("id", Chapter::getId)
                .add("story", c -> c.getStory().getId())
                .add("chapterNumber", Chapter::getChapterNumber)
                .add("title", Chapter::getTitle)
                .add("isPublished", Chapter::getIsPublished)
                .add("viewCount", Chapter::getViewCount)
                .add("content", Chapter::getContent)
                .add("pageCount", Chapter::getPageCount)
                .add("isBanned", Chapter::getIsBanned)
                .add(
                        "currentModeration",
                        u ->
                                u.getCurrentModeration() == null
                                        ? null
                                        : u.getCurrentModeration().getId())
                .build();
    }
}
