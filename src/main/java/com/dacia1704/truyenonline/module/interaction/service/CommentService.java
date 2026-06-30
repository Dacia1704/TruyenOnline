package com.dacia1704.truyenonline.module.interaction.service;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentCreateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.request.CommentUpdateRequest;
import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Comment;
import com.dacia1704.truyenonline.module.interaction.mapper.CommentMapper;
import com.dacia1704.truyenonline.module.interaction.repository.CommentRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
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
public class CommentService {
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ChapterRepository chapterRepository;
    StoryRepository storyRepository;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentChapter(String chapterId, int page, int size) {

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Page<Comment> commentPage =
                commentRepository.findByChapterIdAndParentIsNull(chapterId, pageable);

        List<CommentResponse> content =
                commentPage.getContent().stream().map(commentMapper::toCommentResponse).toList();

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
        Chapter chapter =
                chapterRepository
                        .findById(request.getChapterId())
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Comment parent =
                commentRepository
                        .findById(request.getParentId())
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        Comment comment =
                Comment.builder()
                        .chapter(chapter)
                        .story(chapter.getStory())
                        .user(user)
                        .content(request.getContent())
                        .parent(parent)
                        .build();
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse updateMyComment(String id, CommentUpdateRequest request) {
        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(comment.getUser().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
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
    }
}
