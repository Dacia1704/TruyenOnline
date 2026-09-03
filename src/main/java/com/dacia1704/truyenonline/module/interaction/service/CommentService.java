package com.dacia1704.truyenonline.module.interaction.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentBanRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentCreateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUnbanRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUpdateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Comment;
import com.dacia1704.truyenonline.module.interaction.entity.CommentType;
import com.dacia1704.truyenonline.module.interaction.mapper.CommentMapper;
import com.dacia1704.truyenonline.module.interaction.repository.CommentRepository;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class CommentService {
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ChapterRepository chapterRepository;
    StoryRepository storyRepository;
    UserRepository userRepository;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentChapter(String chapterId, int page, int size) {

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Page<Comment> commentPage =
                commentRepository.findByChapterIdAndParentIsNull(chapterId, pageable);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        User user = userRepository.findById(userId).orElse(null);

        List<CommentResponse> content =
                commentPage.getContent().stream()
                        .map(
                                comment -> {
                                    CommentResponse commentResponse =
                                            commentMapper.toCommentResponse(comment);
                                    log.info(commentResponse.toString());
                                    if (Boolean.FALSE.equals(comment.getIsBanned())) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    if ((user != null
                                                    && user.getRoles().stream()
                                                            .anyMatch(
                                                                    role ->
                                                                            "ADMIN"
                                                                                    .equals(
                                                                                            role
                                                                                                    .getName())))
                                            || (user != null
                                                    && user.getRoles().stream()
                                                            .anyMatch(
                                                                    role ->
                                                                            "UPLOADER"
                                                                                    .equals(
                                                                                            role
                                                                                                    .getName()))
                                                    && comment.getChapter()
                                                            .getStory()
                                                            .getUploader()
                                                            .getId()
                                                            .equals(userId))) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    if (comment.getUser().getId().equals(userId)) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    commentResponse.setContent(
                                            "Nội dung bình luận đã bị ẩn do vi phạm tiêu chuẩn cộng"
                                                    + " đồng.");
                                    return commentResponse;
                                })
                        .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(commentPage.getSize())
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .data(content)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentStory(String storyId, int page, int size) {

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Page<Comment> commentPage =
                commentRepository.findByStoryIdAndParentIsNull(storyId, pageable);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<CommentResponse> content =
                commentPage.getContent().stream()
                        .map(
                                comment -> {
                                    CommentResponse commentResponse =
                                            commentMapper.toCommentResponse(comment);
                                    log.info(commentResponse.toString());
                                    if (Boolean.FALSE.equals(comment.getIsBanned())) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    if ((user != null
                                                    && user.getRoles().stream()
                                                            .anyMatch(
                                                                    role ->
                                                                            "ADMIN"
                                                                                    .equals(
                                                                                            role
                                                                                                    .getName())))
                                            || (user != null
                                                    && user.getRoles().stream()
                                                            .anyMatch(
                                                                    role ->
                                                                            "UPLOADER"
                                                                                    .equals(
                                                                                            role
                                                                                                    .getName()))
                                                    && comment.getChapter()
                                                            .getStory()
                                                            .getUploader()
                                                            .getId()
                                                            .equals(userId))) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    if (comment.getUser().getId().equals(userId)) {
                                        commentResponse.setContent(comment.getContent());
                                        return commentResponse;
                                    }
                                    commentResponse.setContent(
                                            "Nội dung bình luận đã bị ẩn do vi phạm tiêu chuẩn cộng"
                                                    + " đồng.");
                                    return commentResponse;
                                })
                        .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(commentPage.getSize())
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .data(content)
                .build();
    }

    public CommentResponse createMyComment(CommentCreateRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Story story =
                storyRepository
                        .findById(request.getStoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        Comment comment =
                Comment.builder()
                        .story(story)
                        .user(user)
                        .type(request.getType())
                        .content(request.getContent())
                        .build();
        if (StringUtils.hasText(request.getParentId())) {
            comment.setParent(
                    commentRepository
                            .findById(request.getParentId())
                            .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND)));
        }
        if (request.getType().equals(CommentType.CHAPTER)) {
            comment.setChapter(
                    chapterRepository
                            .findById(request.getChapterId())
                            .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND)));
        }
        comment = commentRepository.save(comment);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.COMMENT,
                comment.getId(),
                null,
                buildAuditLogComment(comment),
                null);

        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse updateMyComment(String id, CommentUpdateRequest request) {
        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        var oldValue = buildAuditLogComment(comment);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(comment.getUser().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.COMMENT,
                comment.getId(),
                oldValue,
                buildAuditLogComment(comment),
                null);

        return commentMapper.toCommentResponse(comment);
    }

    public void deleteMyComment(String id) {
        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(comment.getUser().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }
        commentRepository.delete(comment);
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.COMMENT,
                comment.getId(),
                buildAuditLogComment(comment),
                null,
                null);
    }

    public CommentResponse banComment(String commentId, CommentBanRequest request) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        if (Boolean.TRUE.equals(comment.getIsBanned()))
            throw new AppException(ErrorCode.COMMENT_ALREADY_BANNED);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<String> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
        if (!authorities.contains("ROLE_ADMIN")
                && !comment.getStory().getUploader().getId().equals(userId))
            throw new AppException(ErrorCode.NO_PERMISSION);
        var oldValue = buildAuditLogComment(comment);
        comment.setIsBanned(true);
        comment = commentRepository.save(comment);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(commentId)
                        .objectType(ModerationObjectType.COMMENT)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.BAN,
                AuditObjectType.COMMENT,
                commentId,
                oldValue,
                buildAuditLogComment(comment),
                request.getReason());
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse unbanComment(String commentId, CommentUnbanRequest request) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        if (Boolean.FALSE.equals(comment.getIsBanned()))
            throw new AppException(ErrorCode.STORY_NOT_GET_BANNED);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<String> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
        if (!authorities.contains("ROLE_ADMIN")
                && !comment.getStory().getUploader().getId().equals(userId))
            throw new AppException(ErrorCode.NO_PERMISSION);
        var oldValue = buildAuditLogComment(comment);
        comment.setIsBanned(false);
        comment = commentRepository.save(comment);

        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(commentId)
                        .objectType(ModerationObjectType.COMMENT)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build());

        auditLogService.log(
                AuditAction.UNBAN,
                AuditObjectType.COMMENT,
                commentId,
                oldValue,
                buildAuditLogComment(comment),
                request.getReason());

        return commentMapper.toCommentResponse(comment);
    }

    private Map<String, Object> buildAuditLogComment(Comment comment) {
        if (comment == null) {
            return Map.of();
        }

        return AuditMapper.of(comment)
                .add("id", Comment::getId)
                .add("user", c -> c.getUser().getId())
                .add("type", Comment::getType)
                .add("story", c -> c.getStory().getId())
                .add("chapter", u -> u.getChapter() == null ? null : u.getChapter().getId())
                .add("content", Comment::getContent)
                .add("parent", u -> u.getParent() == null ? null : u.getParent().getId())
                .add(
                        "replies",
                        u ->
                                u.getReplies() == null
                                        ? List.of()
                                        : u.getReplies().stream().map(Comment::getContent).toList())
                .add("isBanned", Comment::getIsBanned)
                .add(
                        "currentModeration",
                        u ->
                                u.getCurrentModeration() == null
                                        ? null
                                        : u.getCurrentModeration().getId())
                .build();
    }
}
