package com.dacia1704.truyenonline.module.story.service;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.mapper.AuditMapper;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.administration.service.ModerationActionService;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.repository.ChapterRepository;
import com.dacia1704.truyenonline.module.chapter.service.ChapterService;
import com.dacia1704.truyenonline.module.interaction.service.CommentService;
import com.dacia1704.truyenonline.module.interaction.service.ReadingHistoryService;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.module.media.service.CloudinaryService;
import com.dacia1704.truyenonline.module.media.service.MediaFileService;
import com.dacia1704.truyenonline.module.story.dto.request.*;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.*;
import com.dacia1704.truyenonline.module.story.mapper.GenreMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryAuthorMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.story.mapper.StoryPublishRequestMapper;
import com.dacia1704.truyenonline.module.story.repository.GenreRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryPublishRequestRepository;
import com.dacia1704.truyenonline.module.story.repository.StoryRepository;
import com.dacia1704.truyenonline.module.story.repository.specification.StoryPublishRequestSpecification;
import com.dacia1704.truyenonline.module.story.repository.specification.StorySpecification;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.dacia1704.truyenonline.shared.utils.StringUtils;
import java.util.HashSet;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class StoryService {

    StoryRepository storyRepository;
    StoryMapper storyMapper;
    StoryPublishRequestRepository storyPublishRequestRepository;
    StoryPublishRequestMapper storyPublishRequestMapper;
    UserRepository userRepository;
    CloudinaryService cloudinaryService;
    UserService userService;
    ModerationActionService moderationActionService;
    AuditLogService auditLogService;
    AuthorService authorService;
    StoryAuthorMapper storyAuthorMapper;
    MediaFileService mediaFileService;
    ChapterRepository chapterRepository;
    GenreMapper genreMapper;
    GenreRepository genreRepository;
    ChapterService chapterService;
    CommentService commentService;
    ReadingHistoryService readingHistoryService;

    String folderPath = "truyenonline/stories/%s";

    // lấy toàn bộ truyện có phân trang, search theo tên, filter thuộc tính
    public PageResponse<StoryResponse> getStories(int page, int size, StoryFilter filters) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size);

        Specification<Story> spec = StorySpecification.filterStories(filters);

        Page<Story> storyPage = storyRepository.findAll(spec, pageable);

        List<StoryResponse> storyResponses =
                storyPage.getContent().stream()
                        .map(
                                story -> {
                                    StoryResponse response = storyMapper.toStoryResponse(story);
                                    if (story.getAuthors() != null) {
                                        response.setAuthors(
                                                story.getStoryAuthors().stream()
                                                        .map(
                                                                storyAuthorMapper
                                                                        ::toStoryAuthorResponse)
                                                        .toList());
                                    }
                                    if (story.getGenres() != null) {
                                        response.setGenres(
                                                story.getGenres().stream()
                                                        .map(genreMapper::toGenreResponse)
                                                        .toList());
                                    }

                                    return response;
                                })
                        .toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(storyPage.getSize())
                .totalPages(storyPage.getTotalPages())
                .totalElements(storyPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    // lấy toàn bộ truyện có phân trang, search theo tên, filter thuộc tính
    public PageResponse<StoryResponse> getStoriesByAdmin(
            int page, int size, StoryAdminFilter filters) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isUploader =
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_UPLOADER"));
        if (isUploader) filters.setUploaderId(userId);

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size);

        Specification<Story> spec = StorySpecification.filterStories(filters);

        Page<Story> storyPage = storyRepository.findAll(spec, pageable);

        List<StoryResponse> storyResponses =
                storyPage.getContent().stream()
                        .map(
                                story -> {
                                    StoryResponse response = storyMapper.toStoryResponse(story);
                                    if (story.getAuthors() != null) {
                                        response.setAuthors(
                                                story.getStoryAuthors().stream()
                                                        .map(
                                                                storyAuthorMapper
                                                                        ::toStoryAuthorResponse)
                                                        .toList());
                                    }
                                    if (story.getGenres() != null) {
                                        response.setGenres(
                                                story.getGenres().stream()
                                                        .map(genreMapper::toGenreResponse)
                                                        .toList());
                                    }

                                    return response;
                                })
                        .toList();

        return PageResponse.<StoryResponse>builder()
                .currentPage(page)
                .pageSize(storyPage.getSize())
                .totalPages(storyPage.getTotalPages())
                .totalElements(storyPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public StoryResponse getStoryBySlug(String slug) {
        Story story =
                storyRepository
                        .findBySlug(slug)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        if (Boolean.TRUE.equals(story.getIsBanned())
                || Boolean.FALSE.equals(story.getIsPublished())) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            User user =
                    userRepository
                            .findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            boolean isUploaderOrAdmin =
                    story.getUploader().getId().equals(userId)
                            || user.getRoles().stream()
                                    .anyMatch(role -> "ADMIN".equals(role.getName()));
            if (Boolean.TRUE.equals(story.getIsBanned()) && !isUploaderOrAdmin) {
                throw new AppException(ErrorCode.STORY_ALREADY_BANNED);
            }
            if (Boolean.FALSE.equals(story.getIsPublished()) && !isUploaderOrAdmin) {
                throw new AppException(ErrorCode.STORY_NOT_PUBLISHED);
            }
        }

        StoryResponse response = storyMapper.toStoryResponse(story);

        // Lấy danh sách tác giả từ helper method của entity Story và map sang DTO
        if (story.getAuthors() != null) {
            response.setAuthors(
                    story.getStoryAuthors().stream()
                            .map(storyAuthorMapper::toStoryAuthorResponse)
                            .toList());
        }
        if (story.getGenres() != null) {
            response.setGenres(
                    story.getGenres().stream().map(genreMapper::toGenreResponse).toList());
        }

        return response;
    }

    public StoryResponse getStoryById(String id) {
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
                story.getUploader().getId().equals(userId)
                        || user.getRoles().stream()
                                .anyMatch(role -> "ADMIN".equals(role.getName()));
        if (Boolean.TRUE.equals(story.getIsBanned()) && !isUploaderOrAdmin) {
            throw new AppException(ErrorCode.STORY_ALREADY_BANNED);
        }
        if (Boolean.FALSE.equals(story.getIsPublished()) && !isUploaderOrAdmin) {
            throw new AppException(ErrorCode.STORY_NOT_PUBLISHED);
        }

        StoryResponse response = storyMapper.toStoryResponse(story);

        // Lấy danh sách tác giả từ helper method của entity Story và map sang DTO
        if (story.getAuthors() != null) {
            response.setAuthors(
                    story.getStoryAuthors().stream()
                            .map(storyAuthorMapper::toStoryAuthorResponse)
                            .toList());
        }
        if (story.getGenres() != null) {
            response.setGenres(
                    story.getGenres().stream().map(genreMapper::toGenreResponse).toList());
        }

        return response;
    }

    public StoryResponse createStory(StoryCreateRequest request) {
        Story story = storyMapper.toStory(request);
        User uploader = userService.getCurrentUser();
        story.setUploader(uploader);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> fetchedGenres = genreRepository.findAllById(request.getGenreIds());
            if (fetchedGenres.size() != request.getGenreIds().size()) {
                throw new AppException(ErrorCode.GENRE_NOT_FOUND);
            }
            story.setGenres(new HashSet<>(fetchedGenres));
        }

        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));

        story = storyRepository.save(story);

        if (request.getCoverImageFile() != null && !request.getCoverImageFile().isEmpty()) {
            String path = String.format(folderPath, story.getId());
            CloudinaryUploadResult fileUploadResult =
                    cloudinaryService.uploadImage(request.getCoverImageFile(), path);
            mediaFileService.createMediaFile(fileUploadResult);
            story.setCoverImageUrl(fileUploadResult.getSecureUrl());
        }
        story = storyRepository.save(story);

        if (request.getAuthors() != null && !request.getAuthors().isEmpty()) {
            return authorService.updateStoryAuthors(story.getId(), request.getAuthors());
        }

        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.STORY,
                story.getId(),
                null,
                buildAuditLogStory(story),
                null);

        return storyMapper.toStoryResponse(story);
    }

    public StoryResponse updateStory(String storyId, StoryUpdateRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        var oldValue = buildAuditLogStory(story);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> fetchedGenres = genreRepository.findAllById(request.getGenreIds());

            // Kiểm tra xem ID truyền lên có tồn tại hết trong DB không (tuỳ chọn nhưng nên có)
            if (fetchedGenres.size() != request.getGenreIds().size()) {
                throw new AppException(ErrorCode.GENRE_NOT_FOUND);
            }

            // Ép kiểu sang HashSet vì Entity Story khai báo Set<Genre>
            story.setGenres(new HashSet<>(fetchedGenres));
        }

        if (request.getCoverImageUrl() != null
                && !request.getCoverImageUrl().isEmpty()
                && request.getCoverImageFile() != null
                && !request.getCoverImageFile().isEmpty()
                && story.getCoverImageUrl() != null
                && !story.getCoverImageUrl().isEmpty()) {
            mediaFileService.decreaseReference(story.getCoverImageUrl());
        }

        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().isEmpty()) {
            story.setCoverImageUrl(request.getCoverImageUrl());
        }

        if (request.getCoverImageFile() != null && !request.getCoverImageFile().isEmpty()) {

            String path = String.format(folderPath, story.getId());

            CloudinaryUploadResult fileUploadResult =
                    cloudinaryService.uploadImage(request.getCoverImageFile(), path);

            mediaFileService.createMediaFile(fileUploadResult);
            story.setCoverImageUrl(fileUploadResult.getSecureUrl());
        }

        storyMapper.updateStory(story, request);
        story.setSlug(generateSlug(request.getTitle()));
        story.setTitleNoAccent(StringUtils.removeAccent(request.getTitle()));
        story = storyRepository.save(story);

        if (request.getAuthors() != null && !request.getAuthors().isEmpty()) {
            return authorService.updateStoryAuthors(story.getId(), request.getAuthors());
        }
        auditLogService.log(
                AuditAction.UPDATE,
                AuditObjectType.STORY,
                storyId,
                oldValue,
                buildAuditLogStory(story),
                null);
        return storyMapper.toStoryResponse(story);
    }

    public void deleteStory(String id) {
            Story story =
                    storyRepository
                            .findById(id)
                            .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
            mediaFileService.decreaseReference(story.getCoverImageUrl());

            readingHistoryService.deleteReadingHistoryByStory(id);
            List<Chapter> chapters = chapterRepository.findByStoryId(id);
            for (Chapter chapter : chapters) {
                chapterService.deleteChapter(chapter.getId());
            }

            commentService.deleteCommentByStory(id);

            auditLogService.log(
                    AuditAction.DELETE,
                    AuditObjectType.STORY,
                    id,
                    buildAuditLogStory(story),
                    null,
                    null);
            storyRepository.delete(story);
    }

    public void deletePublishRequest(String id) {
        StoryPublishRequest storyPublishRequest =
                storyPublishRequestRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new AppException(ErrorCode.STORY_PUBLISH_REQUEST_NOT_FOUND));
        storyPublishRequestRepository.deleteById(id);
        auditLogService.log(
                AuditAction.DELETE,
                AuditObjectType.PUBLISH_REQUEST,
                id,
                buildAuditLogPublishRequest(storyPublishRequest),
                null,
                null);
    }

    public StoryPublishRequestResponse requestPublish(
            String storyId, StoryPublishRequestCreateRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        if (!userId.equals(story.getUploader().getId())) {
            throw new AppException(ErrorCode.NO_PERMISSION);
        }

        List<StoryPublishRequest> storyPublishRequests =
                storyPublishRequestRepository.findAllByStoryId(storyId);
        if (story.getIsPublished()
                && storyPublishRequests.stream()
                        .anyMatch(
                                publishRequest ->
                                        publishRequest.getStatus()
                                                == StoryPublishRequestStatus.APPROVED)) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_APPROVED);
        }

        if (storyPublishRequests.stream()
                .anyMatch(
                        publishRequest ->
                                publishRequest.getStatus() == StoryPublishRequestStatus.PENDING)) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_PENDING);
        }
        long x = chapterRepository.countByStory_Id(storyId);
        if (chapterRepository.countByStory_Id(storyId) < 1) {
            throw new AppException(ErrorCode.NEED_AT_LEAST_A_CHAPTER_TO_PUBLISH);
        }

        StoryPublishRequest storyPublishRequest = new StoryPublishRequest();
        storyPublishRequest.setRequesterNote(request.getRequesterNote());
        storyPublishRequest.setStory(story);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.PENDING);

        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        auditLogService.log(
                AuditAction.CREATE,
                AuditObjectType.PUBLISH_REQUEST,
                storyPublishRequest.getId(),
                null,
                buildAuditLogPublishRequest(storyPublishRequest),
                null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse approvePublishRequest(
            String publishRequestId, StoryPublishRequestReviewRequest request) {
        User user = userService.getCurrentUser();
        StoryPublishRequest storyPublishRequest =
                storyPublishRequestRepository
                        .findById(publishRequestId)
                        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        var oldValue = buildAuditLogPublishRequest(storyPublishRequest);
        if (storyPublishRequest.getStatus() != StoryPublishRequestStatus.PENDING) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_CONFIRMED);
        }

        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.APPROVED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);

        Story story = storyPublishRequest.getStory();
        story.setIsPublished(true);
        storyRepository.save(story);
        auditLogService.log(
                AuditAction.APPROVE,
                AuditObjectType.PUBLISH_REQUEST,
                storyPublishRequest.getId(),
                oldValue,
                buildAuditLogPublishRequest(storyPublishRequest),
                null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public StoryPublishRequestResponse rejectPublishRequest(
            String publishRequestId, StoryPublishRequestReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        StoryPublishRequest storyPublishRequest =
                storyPublishRequestRepository
                        .findById(publishRequestId)
                        .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        var oldValue = buildAuditLogPublishRequest(storyPublishRequest);
        if (storyPublishRequest.getStatus() != StoryPublishRequestStatus.PENDING) {
            throw new AppException(ErrorCode.STORY_PUBLISH_REQUEST_CONFIRMED);
        }
        storyPublishRequest.setReviewerNote(request.getReviewerNote());
        storyPublishRequest.setReviewer(user);
        storyPublishRequest.setStatus(StoryPublishRequestStatus.REJECTED);
        storyPublishRequest = storyPublishRequestRepository.save(storyPublishRequest);
        auditLogService.log(
                AuditAction.REJECT,
                AuditObjectType.PUBLISH_REQUEST,
                storyPublishRequest.getId(),
                oldValue,
                buildAuditLogPublishRequest(storyPublishRequest),
                null);

        return storyPublishRequestMapper.toStoryPublishRequestResponse(storyPublishRequest);
    }

    public PageResponse<StoryPublishRequestResponse> getPublishRequests(
            int page,
            int size,
            String storyId,
            StoryPublishRequestStatus status,
            String uploaderId) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());

        Specification<StoryPublishRequest> spec =
                StoryPublishRequestSpecification.filterRequests(storyId, status, uploaderId);

        Page<StoryPublishRequest> storyPublishRequestPage =
                storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream()
                        .map(storyPublishRequestMapper::toStoryPublishRequestResponse)
                        .toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public PageResponse<StoryPublishRequestResponse> getMyPublishRequests(
            int page, int size, String storyId, StoryPublishRequestStatus status) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Specification<StoryPublishRequest> spec =
                StoryPublishRequestSpecification.filterRequests(storyId, status, userId);
        Page<StoryPublishRequest> storyPublishRequestPage =
                storyPublishRequestRepository.findAll(spec, pageable);
        List<StoryPublishRequestResponse> storyResponses =
                storyPublishRequestPage.getContent().stream()
                        .map(storyPublishRequestMapper::toStoryPublishRequestResponse)
                        .toList();
        return PageResponse.<StoryPublishRequestResponse>builder()
                .currentPage(page)
                .pageSize(storyPublishRequestPage.getSize())
                .totalPages(storyPublishRequestPage.getTotalPages())
                .totalElements(storyPublishRequestPage.getTotalElements())
                .data(storyResponses)
                .build();
    }

    public StoryResponse banStory(String storyId, StoryBanRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (Boolean.TRUE.equals(story.getIsBanned()))
            throw new AppException(ErrorCode.STORY_ALREADY_BANNED);
        var oldValue = buildAuditLogStory(story);
        story.setIsBanned(true);
        story = storyRepository.save(story);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(storyId)
                        .objectType(ModerationObjectType.STORY)
                        .actionType(ModerationActionType.BAN)
                        .violationType(request.getViolationType())
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.BAN,
                AuditObjectType.STORY,
                storyId,
                oldValue,
                buildAuditLogStory(story),
                request.getReason());
        return storyMapper.toStoryResponse(story);
    }

    public StoryResponse unbanStory(String storyId, StoryUnbanRequest request) {
        Story story =
                storyRepository
                        .findById(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_FOUND));
        if (Boolean.FALSE.equals(story.getIsBanned()))
            throw new AppException(ErrorCode.STORY_NOT_GET_BANNED);
        var oldValue = buildAuditLogStory(story);
        story.setIsBanned(false);
        story = storyRepository.save(story);
        moderationActionService.createModerationAction(
                ModerationActionCreateRequest.builder()
                        .objectId(storyId)
                        .objectType(ModerationObjectType.STORY)
                        .actionType(ModerationActionType.UNBAN)
                        .reason(request.getReason())
                        .build());
        auditLogService.log(
                AuditAction.UNBAN,
                AuditObjectType.STORY,
                storyId,
                oldValue,
                buildAuditLogStory(story),
                request.getReason());
        return storyMapper.toStoryResponse(story);
    }

    private String generateSlug(String title) {
        // 1. Tạo title không dấu
        String titleNoAccent = StringUtils.removeAccent(title);

        // 2. Dọn dẹp ký tự đặc biệt, thay khoảng trắng thành gạch ngang
        String baseSlug =
                titleNoAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String finalSlug = baseSlug;
        int counter = 1;
        while (storyRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }

    public Map<String, Object> buildAuditLogStory(Story story) {
        if (story == null) {
            return Map.of();
        }

        return AuditMapper.of(story)
                .add("id", Story::getId)
                .add("uploader", s -> s.getUploader().getId())
                .add("title", Story::getTitle)
                .add("description", Story::getDescription)
                .add("storyType", Story::getStoryType)
                .add("status", Story::getStatus)
                .add("isPublished", Story::getIsPublished)
                .add("freeChapterLimit", Story::getFreeChapterLimit)
                .add("viewCount", Story::getViewCount)
                .add(
                        "genres",
                        u ->
                                u.getGenres() == null
                                        ? List.of()
                                        : u.getGenres().stream().map(Genre::getName).toList())
                .add("isBanned", Story::getIsBanned)
                .add(
                        "bookmarks",
                        u ->
                                u.getBookmarks() == null
                                        ? List.of()
                                        : u.getBookmarks().stream()
                                                .map(book -> book.getUser().getId())
                                                .toList())
                .add(
                        "storyAuthors",
                        u ->
                                u.getStoryAuthors() == null
                                        ? List.of()
                                        : u.getStoryAuthors().stream()
                                                .map(StoryAuthor::getAuthorId)
                                                .toList())
                .add(
                        "currentModeration",
                        u ->
                                u.getCurrentModeration() == null
                                        ? null
                                        : u.getCurrentModeration().getId())
                .build();
    }

    public Map<String, Object> buildAuditLogPublishRequest(
            StoryPublishRequest storyPublishRequest) {
        if (storyPublishRequest == null) {
            return Map.of();
        }

        return AuditMapper.of(storyPublishRequest)
                .add("id", StoryPublishRequest::getId)
                .add("story", s -> s.getStory().getId())
                .add("requesterNote", StoryPublishRequest::getRequesterNote)
                .add("reviewerNote", StoryPublishRequest::getReviewerNote)
                .add("reviewer", u -> u.getReviewer() == null ? null : u.getReviewer().getId())
                .add("status", StoryPublishRequest::getStatus)
                .build();
    }
}
