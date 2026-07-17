package com.dacia1704.truyenonline.module.administration.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.administration.mapper.ModerationActionMapper;
import com.dacia1704.truyenonline.module.administration.repository.ModerationActionRepository;
import com.dacia1704.truyenonline.module.administration.repository.specification.ModerationActionSpecification;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.repository.specification.ChapterSpecification;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ModerationActionService {
    ModerationActionMapper moderationActionMapper;
    ModerationActionRepository moderationActionRepository;
    UserRepository userRepository;

    public PageResponse<ModerationActionResponse> getModerationActions(
            ModerationActionPageRequest request) {

        int pageNo = request.getPage() > 0 ? request.getPage() - 1 : 0;

        Pageable pageable = PageRequest.of(
                pageNo,
                request.getSize(),
                Sort.by("createdAt").descending()
        );

        Specification<ModerationAction> spec =
                ModerationActionSpecification.filterModerationActions(
                        request.getObjectId(),
                        request.getObjectType(),
                        request.getActionType(),
                        request.getViolationType()
                );

        Page<ModerationAction> moderationPage =
                moderationActionRepository.findAll(spec, pageable);

        List<ModerationActionResponse> responses =
                moderationPage.getContent()
                        .stream()
                        .map(moderationActionMapper::toResponse)
                        .toList();

        return PageResponse.<ModerationActionResponse>builder()
                .currentPage(request.getPage())
                .pageSize(moderationPage.getSize())
                .totalPages(moderationPage.getTotalPages())
                .totalElements(moderationPage.getTotalElements())
                .data(responses)
                .build();
    }

    public ModerationActionResponse createModerationAction(
            ModerationActionCreateRequest request) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User currentAdmin =  userRepository
                .findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ModerationAction moderationAction =
                moderationActionMapper.toModerationAction(request);

        moderationAction.setAdmin(currentAdmin);

        moderationAction = moderationActionRepository.save(moderationAction);

        return moderationActionMapper.toResponse(moderationAction);
    }
}
