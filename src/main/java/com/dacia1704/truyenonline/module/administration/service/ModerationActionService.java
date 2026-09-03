package com.dacia1704.truyenonline.module.administration.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.ModerationActionMapper;
import com.dacia1704.truyenonline.module.administration.repository.ModerationActionRepository;
import com.dacia1704.truyenonline.module.administration.repository.specification.ModerationActionSpecification;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.mapper.ChapterMapper;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.interaction.entity.Comment;
import com.dacia1704.truyenonline.module.interaction.mapper.CommentMapper;
import com.dacia1704.truyenonline.module.interaction.repository.CommentRepository;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ModerationActionService {
    ModerationActionMapper moderationActionMapper;
    StoryMapper storyMapper;
    ChapterMapper chapterMapper;
    UserMapper userMapper;
    CommentMapper commentMapper;
    ModerationActionRepository moderationActionRepository;
    UserRepository userRepository;
    StoryRepository storyRepository;
    CommentRepository commentRepository;
    ChapterRepository chapterRepository;

    public PageResponse<ModerationActionResponse> getModerationActions(
            ModerationActionPageRequest request) {

        int pageNo = request.getPage() > 0 ? request.getPage() - 1 : 0;

        Pageable pageable =
                PageRequest.of(pageNo, request.getSize(), Sort.by("createdAt").descending());

        Specification<ModerationAction> spec =
                ModerationActionSpecification.filterModerationActions(
                        request.getObjectId(),
                        request.getObjectType(),
                        request.getActionType(),
                        request.getViolationType());

        Page<ModerationAction> moderationPage = moderationActionRepository.findAll(spec, pageable);

        List<ModerationActionResponse> responses =
                moderationPage.getContent().stream()
                        .map(
                                (moderationAction) -> {
                                    ModerationActionResponse moderationActionResponse =
                                            moderationActionMapper.toResponse(moderationAction);
                                    String id = moderationAction.getObjectId();
                                    if (moderationAction.getObjectType()
                                            == ModerationObjectType.STORY) {
                                        Story story =
                                                storyRepository
                                                        .findById(id)
                                                        .orElseThrow(
                                                                () ->
                                                                        new AppException(
                                                                                ErrorCode
                                                                                        .STORY_NOT_FOUND));
                                        moderationActionResponse.setStoryResponse(
                                                storyMapper.toStoryResponse(story));
                                    }
                                    if (moderationAction.getObjectType()
                                            == ModerationObjectType.CHAPTER) {
                                        Chapter chapter =
                                                chapterRepository
                                                        .findById(id)
                                                        .orElseThrow(
                                                                () ->
                                                                        new AppException(
                                                                                ErrorCode
                                                                                        .CHAPTER_NOT_FOUND));
                                        moderationActionResponse.setChapterResponse(
                                                chapterMapper.toChapterResponse(chapter));
                                    }
                                    if (moderationAction.getObjectType()
                                            == ModerationObjectType.COMMENT) {
                                        Comment comment =
                                                commentRepository
                                                        .findById(id)
                                                        .orElseThrow(
                                                                () ->
                                                                        new AppException(
                                                                                ErrorCode
                                                                                        .COMMENT_NOT_FOUND));
                                        moderationActionResponse.setCommentResponse(
                                                commentMapper.toCommentResponse(comment));
                                    }
                                    return moderationActionResponse;
                                })
                        .toList();

        return PageResponse.<ModerationActionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(moderationPage.getSize())
                .totalPages(moderationPage.getTotalPages())
                .totalElements(moderationPage.getTotalElements())
                .data(responses)
                .build();
    }

    public ModerationActionResponse createModerationAction(ModerationActionCreateRequest request) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User currentAdmin =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ModerationAction moderationAction = moderationActionMapper.toModerationAction(request);

        moderationAction.setAdmin(currentAdmin);

        moderationAction = moderationActionRepository.save(moderationAction);

        return moderationActionMapper.toResponse(moderationAction);
    }

    public ModerationActionResponse getModrationAction(String id) {
        ModerationAction moderationAction =
                moderationActionRepository.findTopByObjectIdOrderByCreatedAtDesc(id).orElse(null);
        if (moderationAction == null) return null;

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        if (moderationAction.getObjectType() == ModerationObjectType.USER && !userId.equals(id)) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }
        if (moderationAction.getObjectType() == ModerationObjectType.STORY) {
            Story story =
                    storyRepository
                            .findById(id)
                            .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
            if (!story.getUploader().getId().equals(userId)) {
                throw new AppException(ErrorCode.NO_PERMISSION);
            }
        }
        if (moderationAction.getObjectType() == ModerationObjectType.CHAPTER) {
            Chapter chapter =
                    chapterRepository
                            .findById(id)
                            .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
            if (!chapter.getStory().getUploader().getId().equals(userId)) {
                throw new AppException(ErrorCode.NO_PERMISSION);
            }
        }
        if (moderationAction.getObjectType() == ModerationObjectType.COMMENT) {
            Comment comment =
                    commentRepository
                            .findById(id)
                            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            if (!comment.getUser().getId().equals(userId)) {
                throw new AppException(ErrorCode.NO_PERMISSION);
            }
        }

        ModerationActionResponse moderationActionResponse =
                moderationActionMapper.toResponse(moderationAction);
        String objectId = moderationAction.getObjectId();
        if (moderationAction.getObjectType() == ModerationObjectType.STORY) {
            Story story =
                    storyRepository
                            .findById(objectId)
                            .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
            moderationActionResponse.setStoryResponse(storyMapper.toStoryResponse(story));
        }
        if (moderationAction.getObjectType() == ModerationObjectType.CHAPTER) {
            Chapter chapter =
                    chapterRepository
                            .findById(objectId)
                            .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
            moderationActionResponse.setChapterResponse(chapterMapper.toChapterResponse(chapter));
        }
        if (moderationAction.getObjectType() == ModerationObjectType.COMMENT) {
            Comment comment =
                    commentRepository
                            .findById(objectId)
                            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            moderationActionResponse.setCommentResponse(commentMapper.toCommentResponse(comment));
        }
        return moderationActionResponse;
    }
}
