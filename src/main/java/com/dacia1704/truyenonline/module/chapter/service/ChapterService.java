package com.dacia1704.truyenonline.module.chapter.service;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.dto.request.*;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterPageResponse;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.mapper.ChapterMapper;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.chapter.repository.specification.ChapterSpecification;
import com.dacia1704.truyenonline.module.story.dto.request.StoryBanRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.story.service.StoryService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.shared.utils.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    StoryService storyService;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;
    ObjectMapper objectMapper;

    public PageResponse<ChapterResponse> getChaptersBySlug(
            int page, int size, String slug, String search) {
        Story story =
                storyRepository
                        .findBySlug(slug)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("chapterNumber").ascending());

        Specification<Chapter> spec = ChapterSpecification.filterChapters(search, story);

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
        if (BigDecimal.valueOf(chapter.getStory().getFreeChapterLimit())
                        .compareTo(chapter.getChapterNumber())
                < 0) {
            throw new AppException(ErrorCode.PREMIUM_REQUIRED);
        }
        List<ChapterPageResponse> pages = chapterPageService.getChapterPagesByChapterId(chapterId);
        ChapterResponse res = chapterMapper.toChapterResponse(chapter);
        res.setContent(chapter.getContent());
        res.setPages(pages);
        return res;
    }

    public ChapterResponse createChapter(String storyId, ChapterCreateRequest request) throws IOException {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        Chapter chapter = chapterMapper.toChapter(request);
        chapter.setTitleNoAccent(StringUtils.removeAccent(chapter.getTitle()));
        chapter.setStory(story);
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse updateChapter(String chapterId, ChapterUpdateRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        chapterMapper.updateChapter(chapter, request);
        chapter = chapterRepository.save(chapter);
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
        String storyId = chapters.get(0).getStory().getId();
        boolean sameStory = chapters.stream()
                .allMatch(chapter -> chapter.getStory().getId().equals(storyId));
        if (!sameStory) {
            throw new AppException(ErrorCode.CHAPTER_NOT_SAME_STORY);
        }

        //check story có phải của uploader này ko
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (!uploaderId.equals(story.getUploader().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        chapters.forEach(chapter -> chapter.setPublished(request.getPublishStatus()));

        chapterRepository.saveAll(chapters);
    }

    public ChapterResponse updateChapterContent(String chapterId, ChapterContentUpdateRequest request) {
        Chapter chapter =
                chapterRepository
                        .findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        chapter.setContent(request.getContent());
        chapter = chapterRepository.save(chapter);
        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse banChapter(String chapterId, ChapterBanRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        if (chapter.isBanned()) {
            throw new AppException(ErrorCode.CHAPTER_ALREADY_BANNED);
        }

        String oldValue;
        String newValue;

        try {
            oldValue = objectMapper.writeValueAsString(chapter);

            chapter.setBanned(true);

            newValue = objectMapper.writeValueAsString(chapter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize chapter", e);
        }

        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(chapterId)
                        .objectType(ModerationObjectType.CHAPTER)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build()
        );

        auditLogService.createAuditLog(
                AuditLogCreateRequest.builder()
                        .action(AuditAction.BAN)
                        .objectType(AuditObjectType.CHAPTER)
                        .objectId(chapterId)
                        .description(request.getReason())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build()
        );

        chapter = chapterRepository.save(chapter);

        return chapterMapper.toChapterResponse(chapter);
    }

    public ChapterResponse unbanChapter(String chapterId, ChapterUnbanRequest request) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        if (!chapter.isBanned()) {
            throw new AppException(ErrorCode.CHAPTER_NOT_GET_BANNED);
        }

        String oldValue;
        String newValue;

        try {
            oldValue = objectMapper.writeValueAsString(chapter);

            chapter.setBanned(false);

            newValue = objectMapper.writeValueAsString(chapter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize chapter", e);
        }

        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(chapterId)
                        .objectType(ModerationObjectType.CHAPTER)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build()
        );

        auditLogService.createAuditLog(
                AuditLogCreateRequest.builder()
                        .action(AuditAction.UNBAN)
                        .objectType(AuditObjectType.CHAPTER)
                        .objectId(chapterId)
                        .description(request.getReason())
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build()
        );

        chapter = chapterRepository.save(chapter);

        return chapterMapper.toChapterResponse(chapter);
    }


    public void deleteChapter(String chapterId) {
        chapterRepository.deleteById(chapterId);
    }
}
